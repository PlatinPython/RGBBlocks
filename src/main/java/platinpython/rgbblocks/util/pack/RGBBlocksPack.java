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
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.metadata.pack.PackMetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.Color;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RGBBlocksPack extends AbstractPackResources implements PreparableReloadListener {
    public static final String TEXTURE_DIRECTORY = "textures/";
    public static final String BLOCK_DIRECTORY = "block/";
    public static final Set<String> NAMESPACES = ImmutableSet.of(RGBBlocks.MOD_ID);
    public static final List<ResourceLocation> NO_RESOURCES = Collections.emptyList();

    private final PackMetadataSection packInfo;
    private Map<ResourceLocation, IoSupplier<InputStream>> resources = new HashMap<>();
    private Map<ResourceLocation, ResourceLocation> textures = new HashMap<>();

    public RGBBlocksPack() {
        super("rgbblocks_virtual_pack", true);
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
        Map<ResourceLocation, IoSupplier<InputStream>> resourceStreams = new HashMap<>();

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

    public Optional<Pair<NativeImage, Optional<IoSupplier<InputStream>>>> generateImage(ResourceLocation modLocation,
                                                                                        ResourceLocation vanillaLocation,
                                                                                        ResourceManager manager) {
        ResourceLocation parentFile = makeTextureID(vanillaLocation);
        try (InputStream inputStream = manager.getResource(parentFile).orElseThrow().open()) {
            NativeImage image = NativeImage.read(inputStream);
            NativeImage transformedImage = this.transformImage(image);
            ResourceLocation metadata = getMetadataLocation(parentFile);
            Optional<IoSupplier<InputStream>> metadataLookup = Optional.empty();
            BufferedReader bufferedReader = null;
            JsonObject metadataJson;
            if (manager.getResource(metadata).isPresent()) {
                try (InputStream metadataStream = manager.getResource(metadata).get().open()) {
                    bufferedReader = new BufferedReader(new InputStreamReader(metadataStream, StandardCharsets.UTF_8));
                    metadataJson = GsonHelper.parse(bufferedReader);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Optional.empty();
                } finally {
                    IOUtils.closeQuietly(bufferedReader);
                }
                JsonObject metaDataJsonForLambda = metadataJson;
                metadataLookup = Optional.of(
                        () -> new ByteArrayInputStream(metaDataJsonForLambda.toString().getBytes()));
            }
            return Optional.of(Pair.of(transformedImage, metadataLookup));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        } catch (NoSuchElementException ignored) {
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
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
        return serializer instanceof PackMetadataSectionSerializer ? (T) this.packInfo : null;
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... fileName) {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return NAMESPACES;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation id) {
        if (this.resources.containsKey(id)) {
            IoSupplier<InputStream> streamGetter = this.resources.get(id);
            if (streamGetter == null) {
                return null;
            }

            try {
                return streamGetter;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void listResources(PackType type, String namespace, String id, ResourceOutput output) {
        if (namespace.equals(RGBBlocks.MOD_ID)) {
            ResourceLocation resLoc = new ResourceLocation(namespace, id);
            output.accept(resLoc, getResource(type, resLoc));
        }
    }
}
