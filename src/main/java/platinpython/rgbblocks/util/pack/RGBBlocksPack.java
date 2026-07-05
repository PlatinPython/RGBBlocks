package platinpython.rgbblocks.util.pack;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.InclusiveRange;
import net.neoforged.fml.ModList;
import net.neoforged.fml.jarcontents.JarContents;
import org.jspecify.annotations.Nullable;
import org.lwjgl.stb.STBImage;
import platinpython.rgbblocks.RGBBlocks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class RGBBlocksPack extends AbstractPackResources {
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
    private static final ImmutableMap<Identifier, Identifier> TEXTURES = MOD_TO_VANILLA_MAP.entrySet()
        .stream()
        .flatMap(RGBBlocksPack::makeIDs)
        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    private final PackMetadataSection packInfo;
    private final HashMap<Identifier, IoSupplier<InputStream>> resources = new HashMap<>();

    public RGBBlocksPack() {
        super(LOCATION_INFO);
        this.packInfo = new PackMetadataSection(
            Component.translatable("rgbblocks.pack_description"),
            new InclusiveRange<>(SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES))
        );
    }

    private static Stream<Map.Entry<Identifier, Identifier>> makeIDs(Map.Entry<String, String> entry) {
        Map.Entry<Identifier, Identifier> paths = Map.entry(
            Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, BLOCK_DIRECTORY + entry.getKey()),
            Identifier.withDefaultNamespace(BLOCK_DIRECTORY + entry.getValue())
        );
        Map.Entry<Identifier, Identifier> texture =
            Map.entry(makeTextureID(paths.getKey()), makeTextureID(paths.getValue()));
        return Stream
            .of(texture, Map.entry(getMetadataLocation(texture.getKey()), getMetadataLocation(texture.getValue())));
    }

    private static Identifier makeTextureID(Identifier id) {
        return id.withPath(path -> TEXTURE_DIRECTORY + path + ".png");
    }

    private static Identifier getMetadataLocation(Identifier id) {
        return id.withSuffix(".mcmeta");
    }

    private @Nullable IoSupplier<InputStream> computeImage(
        Identifier modLocation,
        Identifier vanillaLocation,
        ResourceManager manager
    ) {
        try (InputStream inputStream = manager.getResourceOrThrow(vanillaLocation).open()) {
            NativeImage image = NativeImage.read(inputStream);
            NativeImage transformedImage = image.mappedCopy(ARGB::greyscale);
            return () -> new ByteArrayInputStream(nativeImageToBytes(transformedImage));
        } catch (IOException e) {
            RGBBlocks.LOGGER.error("Error while generating {}", modLocation, e);
            return null;
        }
    }

    private IoSupplier<InputStream> computeMetadata(
        Identifier modLocation,
        Identifier vanillaLocation,
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

    private static byte[] nativeImageToBytes(NativeImage image) throws IOException {
        byte[] bytes;
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream)
        ) {
            if (!image.writeToChannel(writableByteChannel)) {
                throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
            }

            bytes = byteArrayOutputStream.toByteArray();
        }

        return bytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> metadataSerializer) {
        return metadataSerializer == PackMetadataSection.CLIENT_TYPE ? (T) this.packInfo : null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... elements) {
        for (String name : elements) {
            if (!name.equals("pack.png")) {
                continue;
            }
            JarContents contents = ModList.get().getModFileById(RGBBlocks.MOD_ID).getFile().getContents();
            if (!contents.containsFile("logo.png")) {
                return null;
            }
            // noinspection DataFlowIssue
            return () -> contents.get("logo.png").open();
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
    public @Nullable IoSupplier<InputStream> getResource(PackType type, Identifier id) {
        if (TEXTURES.containsKey(id)) {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            IoSupplier<InputStream> streamSupplier;
            if (id.getPath().endsWith(".mcmeta")) {
                // noinspection DataFlowIssue
                streamSupplier = this.resources
                    .computeIfAbsent(id, location -> computeMetadata(location, TEXTURES.get(location), manager));
            } else {
                // noinspection DataFlowIssue
                streamSupplier = this.resources
                    .computeIfAbsent(id, location -> computeImage(location, TEXTURES.get(location), manager));
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
            TEXTURES.forEach((name, ignored) -> {
                if (name.getPath().startsWith(id)) {
                    output.accept(name, getResource(type, name));
                }
            });
        }
    }
}
