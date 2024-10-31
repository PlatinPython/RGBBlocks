package platinpython.rgbblocks.util.pack;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackSource;
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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RGBBlocksPack extends AbstractPackResources implements PreparableReloadListener {
    public static final PackLocationInfo LOCATION_INFO = new PackLocationInfo(
        "rgbblocks_textures", Component.translatable("rgbblocks.pack_title"), PackSource.BUILT_IN, Optional.empty()
    );
    public static final String TEXTURE_DIRECTORY = "textures/";
    public static final String BLOCK_DIRECTORY = "block/";
    public static final Set<String> NAMESPACES = ImmutableSet.of(RGBBlocks.MOD_ID);
    public static final ImmutableMap<String, String> MOD_TO_VANILLA_MAP = ImmutableMap.<String, String>builder()
        .put("concrete", "white_concrete")
        .put("concrete_powder", "white_concrete_powder")
        .put("wool", "white_wool")
        .put("planks", "birch_planks")
        .put("terracotta", "white_terracotta")
        .put("glass", "white_stained_glass")
        .put("glass_pane_top", "white_stained_glass_pane_top")
        .put("glowstone", "glowstone")
        .put("redstone_lamp", "redstone_lamp")
        .put("redstone_lamp_on", "redstone_lamp_on")
        .put("prismarine", "prismarine")
        .put("prismarine_bricks", "prismarine_bricks")
        .put("dark_prismarine", "dark_prismarine")
        .put("sea_lantern", "sea_lantern")
        .build();
    private static final ImmutableMap<ResourceLocation, ResourceLocation> TEXTURES = MOD_TO_VANILLA_MAP.entrySet()
        .stream()
        .map(
            entry -> Pair.of(
                ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, BLOCK_DIRECTORY + entry.getKey()),
                ResourceLocation.withDefaultNamespace(BLOCK_DIRECTORY + entry.getValue())
            )
        )
        .collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));

    private final PackMetadataSection packInfo;
    private Map<ResourceLocation, IoSupplier<InputStream>> resources = new HashMap<>();

    public RGBBlocksPack() {
        super(LOCATION_INFO);
        this.packInfo = new PackMetadataSection(
            Component.translatable("rgbblocks.pack_description"),
            SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
        );
    }

    @Override
    public CompletableFuture<Void> reload(
        PreparationBarrier stage,
        ResourceManager manager,
        ProfilerFiller workerProfiler,
        ProfilerFiller mainProfiler,
        Executor workerExecutor,
        Executor mainExecutor
    ) {
        this.gatherTextureData(manager, mainProfiler);
        return CompletableFuture.supplyAsync(() -> null, workerExecutor)
            .thenCompose(stage::wait)
            .thenAcceptAsync((noResult) -> {}, mainExecutor);
    }

    protected void gatherTextureData(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, IoSupplier<InputStream>> resourceStreams = new HashMap<>();

        TEXTURES.forEach(
            (
                modLocation,
                vanillaLocation
            ) -> generateImage(modLocation, vanillaLocation, Minecraft.getInstance().getResourceManager())
                .ifPresent(pair -> {
                    NativeImage image = pair.getFirst();
                    ResourceLocation textureID = makeTextureID(modLocation);
                    resourceStreams.put(textureID, () -> new ByteArrayInputStream(image.asByteArray()));
                    pair.getSecond()
                        .ifPresent(
                            metadataGetter -> resourceStreams.put(getMetadataLocation(textureID), metadataGetter)
                        );
                })
        );

        this.resources = resourceStreams;
    }

    public static ResourceLocation makeTextureID(ResourceLocation id) {
        return id.withPath(path -> TEXTURE_DIRECTORY + path + ".png");
    }

    public static ResourceLocation getMetadataLocation(ResourceLocation id) {
        return id.withPath(path -> path + ".mcmeta");
    }

    public Optional<Pair<NativeImage, Optional<IoSupplier<InputStream>>>> generateImage(
        ResourceLocation modLocation,
        ResourceLocation vanillaLocation,
        ResourceManager manager
    ) {
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
                    return Optional.empty();
                } finally {
                    IOUtils.closeQuietly(bufferedReader);
                }
                JsonObject metaDataJsonForLambda = metadataJson;
                metadataLookup =
                    Optional.of(() -> new ByteArrayInputStream(metaDataJsonForLambda.toString().getBytes()));
            }
            return Optional.of(Pair.of(transformedImage, metadataLookup));
        } catch (IOException | NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public NativeImage transformImage(NativeImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int oldColor = image.getPixelRGBA(x, y);
                float[] hsb = Color.RGBtoHSB(oldColor & 0xFF, (oldColor >> 8) & 0xFF, (oldColor >> 16) & 0xFF);
                int newColor = Color.HSBtoRGB(0, 0, hsb[2]);
                image.setPixelRGBA(
                    x, y,
                    ((oldColor >> 24) & 0xFF) << 24 | (newColor & 0xFF) << 16 | ((newColor >> 8) & 0xFF) << 8
                        | (newColor >> 16) & 0xFF
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
        return serializer == PackMetadataSection.TYPE ? (T) this.packInfo : null;
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... fileName) {
        return null;
    }

    @Override
    public void close() {}

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
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void listResources(PackType type, String namespace, String id, ResourceOutput output) {
        if (namespace.equals(RGBBlocks.MOD_ID)) {
            this.resources.forEach((name, supplier) -> {
                if (name.getPath().startsWith(id)) {
                    output.accept(name, getResource(type, name));
                }
            });
        }
    }
}
