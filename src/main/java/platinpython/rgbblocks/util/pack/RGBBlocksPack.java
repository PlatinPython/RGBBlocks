package platinpython.rgbblocks.util.pack;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.metadata.pack.PackMetadataSectionSerializer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.Color;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public class RGBBlocksPack extends AbstractPackResources implements PreparableReloadListener {
    public static final String TEXTURE_DIRECTORY = "textures/";
    public static final String BLOCK_DIRECTORY = "block/";
    public static final Set<String> NAMESPACES = ImmutableSet.of(RGBBlocks.MOD_ID);
    public static final List<ResourceLocation> NO_RESOURCES = Collections.emptyList();

    private final PackMetadataSection packInfo;
    private Map<ResourceLocation, Callable<InputStream>> resources = new HashMap<>();
    private Map<ResourceLocation, ResourceLocation> textures = new HashMap<>();

    public RGBBlocksPack() {
        super(new File("rgbblocks_virtual_pack"));
        this.packInfo = new PackMetadataSection(Component.translatable("rgbblocks.pack_description"), 7);
        fillTexturesMap();
    }

    private void fillTexturesMap() {
        Map<String, String> map = new HashMap<>();

        map.put("concrete", "white_concrete");
        map.put("concrete_powder", "white_concrete_powder");
        map.put("wool", "white_wool");
        map.put("planks", "birch_planks");
        map.put("terracotta", "white_terracotta");
        map.put("glass", "white_stained_glass");
        map.put("glass_pane_top", "white_stained_glass_pane_top");
        map.put("glowstone", "glowstone");
        map.put("redstone_lamp", "redstone_lamp");
        map.put("redstone_lamp_on", "redstone_lamp_on");
        map.put("prismarine", "prismarine");
        map.put("prismarine_bricks", "prismarine_bricks");
        map.put("dark_prismarine", "dark_prismarine");
        map.put("sea_lantern", "sea_lantern");

        map.forEach((modName, vanillaName) -> textures.put(
                new ResourceLocation(RGBBlocks.MOD_ID, BLOCK_DIRECTORY + modName),
                new ResourceLocation(BLOCK_DIRECTORY + vanillaName)
        ));
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager manager,
                                          ProfilerFiller workerProfiler, ProfilerFiller mainProfiler,
                                          Executor workerExecutor, Executor mainExecutor) {
        this.gatherTextureData(manager, mainProfiler);
        return CompletableFuture.supplyAsync(() -> null, workerExecutor)
                                .thenCompose(stage::wait)
                                .thenAcceptAsync((noResult) -> {
                                }, mainExecutor);
    }

    protected void gatherTextureData(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, Callable<InputStream>> resourceStreams = new HashMap<>();

        textures.forEach((modLocation, vanillaLocation) -> {
            generateImage(modLocation, vanillaLocation, Minecraft.getInstance().getResourceManager()).ifPresent(
                    pair -> {
                        NativeImage image = pair.getFirst();
                        ResourceLocation textureID = makeTextureID(modLocation);
                        resourceStreams.put(textureID, () -> new ByteArrayInputStream(image.asByteArray()));
                        pair.getSecond()
                            .ifPresent(metadataGetter -> resourceStreams.put(getMetadataLocation(textureID),
                                                                             metadataGetter
                            ));
                    });
        });

        this.resources = resourceStreams;
    }

    public static ResourceLocation makeTextureID(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), TEXTURE_DIRECTORY + id.getPath() + ".png");
    }

    public static ResourceLocation getMetadataLocation(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), id.getPath() + ".mcmeta");
    }

    public Optional<Pair<NativeImage, Optional<Callable<InputStream>>>> generateImage(ResourceLocation modLocation,
                                                                                      ResourceLocation vanillaLocation,
                                                                                      ResourceManager manager) {
        ResourceLocation parentFile = makeTextureID(vanillaLocation);
        try (InputStream inputStream = manager.getResource(parentFile).orElseThrow().open()) {
            NativeImage image = NativeImage.read(inputStream);
            NativeImage transformedImage = this.transformImage(image);
            ResourceLocation metadata = getMetadataLocation(parentFile);
            Optional<Callable<InputStream>> metadataLookup;
            BufferedReader bufferedReader = null;
            JsonObject metadataJson;
            try (InputStream metadataStream = manager.getResource(metadata).orElseThrow().open()) {
                bufferedReader = new BufferedReader(new InputStreamReader(metadataStream, StandardCharsets.UTF_8));
                metadataJson = GsonHelper.parse(bufferedReader);
            } finally {
                IOUtils.closeQuietly(bufferedReader);
            }
            JsonObject metaDataJsonForLambda = metadataJson;
            metadataLookup = Optional.of(() -> new ByteArrayInputStream(metaDataJsonForLambda.toString().getBytes()));
            return Optional.of(Pair.of(transformedImage, metadataLookup));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public NativeImage transformImage(NativeImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int oldColor = image.getPixelRGBA(x, y);
                float[] hsb = Color.RGBtoHSB((oldColor >> 0) & 0xFF, (oldColor >> 8) & 0xFF, (oldColor >> 16) & 0xFF);
                int newColor = Color.HSBtoRGB(0, 0, hsb[2]);
                image.setPixelRGBA(x, y,
                                   ((oldColor >> 24) & 0xFF) << 24 | ((newColor >> 0) & 0xFF) << 16 | ((newColor >> 8) & 0xFF) << 8 | ((newColor >> 16) & 0xFF) << 0
                );
            }
        }
        return image;
    }

    @Override
    public String getName() {
        return Component.translatable("rgbblocks.pack_title").getString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
        return serializer instanceof PackMetadataSectionSerializer ? (T) this.packInfo : null;
    }

    @Override
    protected boolean hasResource(String fileName) {
        return false;
    }

    @Override
    public InputStream getRootResource(String fileName) throws IOException {
        throw new ResourcePackFileNotFoundException(this.file, fileName);
    }

    @Override
    protected InputStream getResource(String fileName) throws IOException {
        throw new ResourcePackFileNotFoundException(this.file, fileName);
    }

    @Override
    public void close() {
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return NAMESPACES;
    }

    @Override
    public boolean hasResource(PackType type, ResourceLocation id) {
        return type == PackType.CLIENT_RESOURCES && (this.resources.containsKey(id));
    }

    @Override
    public InputStream getResource(PackType type, ResourceLocation id) throws IOException {
        if (this.resources.containsKey(id)) {
            Callable<InputStream> streamGetter = this.resources.get(id);
            if (streamGetter == null) {
                throw this.makeFileNotFoundException(type, id);
            }

            try {
                return streamGetter.call();
            } catch (Exception e) {
                e.printStackTrace();
                throw this.makeFileNotFoundException(type, id);
            }
        } else {
            throw this.makeFileNotFoundException(type, id);
        }
    }

    public ResourcePackFileNotFoundException makeFileNotFoundException(PackType type, ResourceLocation id) {
        String path = String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
        return new ResourcePackFileNotFoundException(this.file, path);
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespace, String id,
                                                     Predicate<ResourceLocation> filter) {
        return NO_RESOURCES;
    }

}
