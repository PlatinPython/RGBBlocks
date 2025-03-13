package platinpython.rgbblocks.util.pack;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.metadata.pack.PackMetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.ModList;
import org.jspecify.annotations.Nullable;
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
import java.util.Set;
import java.util.stream.Stream;

public class RGBBlocksPack extends AbstractPackResources {
    public static final String TEXTURE_DIRECTORY = "textures/";
    public static final String BLOCK_DIRECTORY = "block/";
    public static final Set<String> NAMESPACES = ImmutableSet.of(RGBBlocks.MOD_ID);

    private static final ImmutableMap<ResourceLocation, ResourceLocation> RESOURCES =
        Stream.<Map.Entry<String, String>>builder()
            .add(Map.entry("concrete", "white_concrete"))
            .add(Map.entry("concrete_powder", "white_concrete_powder"))
            .add(Map.entry("wool", "white_wool"))
            .add(Map.entry("planks", "birch_planks"))
            .add(Map.entry("terracotta", "white_terracotta"))
            .add(Map.entry("glass", "white_stained_glass"))
            .add(Map.entry("glass_pane_top", "white_stained_glass_pane_top"))
            .add(Map.entry("glowstone", "glowstone"))
            .add(Map.entry("redstone_lamp", "redstone_lamp"))
            .add(Map.entry("redstone_lamp_on", "redstone_lamp_on"))
            .add(Map.entry("prismarine", "prismarine"))
            .add(Map.entry("prismarine_bricks", "prismarine_bricks"))
            .add(Map.entry("dark_prismarine", "dark_prismarine"))
            .add(Map.entry("sea_lantern", "sea_lantern"))
            .build()
            .flatMap(RGBBlocksPack::makeIDs)
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    private final PackMetadataSection packInfo;
    private final HashMap<ResourceLocation, IoSupplier<InputStream>> resources = new HashMap<>();

    public RGBBlocksPack() {
        super("rgbblocks_virtual_pack", true);
        this.packInfo = new PackMetadataSection(
            Component.translatable("rgbblocks.pack_description"),
            SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
        );
    }

    private static Stream<Map.Entry<ResourceLocation, ResourceLocation>> makeIDs(Map.Entry<String, String> entry) {
        Map.Entry<ResourceLocation, ResourceLocation> paths = Map.entry(
            new ResourceLocation(RGBBlocks.MOD_ID, BLOCK_DIRECTORY + entry.getKey()),
            new ResourceLocation(BLOCK_DIRECTORY + entry.getValue())
        );
        Map.Entry<ResourceLocation, ResourceLocation> texture =
            Map.entry(makeTextureID(paths.getKey()), makeTextureID(paths.getValue()));
        return Stream
            .of(texture, Map.entry(getMetadataLocation(texture.getKey()), getMetadataLocation(texture.getValue())));
    }

    private static ResourceLocation makeTextureID(ResourceLocation id) {
        return id.withPath(path -> TEXTURE_DIRECTORY + path + ".png");
    }

    private static ResourceLocation getMetadataLocation(ResourceLocation id) {
        return id.withSuffix(".mcmeta");
    }

    private @Nullable IoSupplier<InputStream> computeImage(
        ResourceLocation modLocation,
        ResourceLocation vanillaLocation,
        ResourceManager manager
    ) {
        try (InputStream inputStream = manager.getResourceOrThrow(vanillaLocation).open()) {
            NativeImage image = NativeImage.read(inputStream);
            NativeImage transformedImage = this.transformImage(image);
            return () -> new ByteArrayInputStream(transformedImage.asByteArray());
        } catch (IOException e) {
            RGBBlocks.LOGGER.error("Error while generating {}", modLocation, e);
            return null;
        }
    }

    private @Nullable IoSupplier<InputStream> computeMetadata(
        ResourceLocation modLocation,
        ResourceLocation vanillaLocation,
        ResourceManager manager
    ) {
        return manager.getResource(vanillaLocation).<IoSupplier<InputStream>>map(resource -> {
            try (
                BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))
            ) {
                JsonObject metadataJson = GsonHelper.parse(bufferedReader);
                return () -> new ByteArrayInputStream(metadataJson.toString().getBytes());
            } catch (IOException e) {
                RGBBlocks.LOGGER.error("Error while generating {}", modLocation, e);
                return null;
            }
        }).orElse(() -> new ByteArrayInputStream("{}".getBytes()));
    }

    private NativeImage transformImage(NativeImage image) {
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getMetadataSection(MetadataSectionSerializer<T> serializer) {
        return serializer instanceof PackMetadataSectionSerializer ? (T) this.packInfo : null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... elements) {
        for (String name : elements) {
            if (!name.equals("pack.png")) {
                continue;
            }
            return IoSupplier.create(ModList.get().getModFileById(RGBBlocks.MOD_ID).getFile().findResource("logo.png"));
        }
        return null;
    }

    @Override
    public void close() {}

    @Override
    public Set<String> getNamespaces(PackType type) {
        return NAMESPACES;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation id) {
        if (RESOURCES.containsKey(id)) {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            IoSupplier<InputStream> streamSupplier;
            if (id.getPath().endsWith(".mcmeta")) {
                // noinspection DataFlowIssue
                streamSupplier = this.resources
                    .computeIfAbsent(id, location -> computeMetadata(location, RESOURCES.get(location), manager));
            } else {
                // noinspection DataFlowIssue
                streamSupplier = this.resources
                    .computeIfAbsent(id, location -> computeImage(location, RESOURCES.get(location), manager));
            }
            try {
                return streamSupplier;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String id, ResourceOutput output) {
        if (namespace.equals(RGBBlocks.MOD_ID)) {
            RESOURCES.forEach((name, ignored) -> {
                if (name.getPath().startsWith(id)) {
                    output.accept(name, getResource(type, name));
                }
            });
        }
    }
}
