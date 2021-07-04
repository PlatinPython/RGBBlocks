package platinpython.rgbblocks.util.pack;

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

import org.apache.commons.io.IOUtils;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.resources.data.PackMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.Color;

public class RGBBlocksPack extends ResourcePack implements IFutureReloadListener {
	public static final String TEXTURE_DIRECTORY = "textures/";
	public static final String BLOCK_DIRECTORY = "block/";
	public static final Set<String> NAMESPACES = ImmutableSet.of(RGBBlocks.MOD_ID);
	public static final List<ResourceLocation> NO_RESOURCES = Collections.emptyList();

	private final PackMetadataSection packInfo;
	private Map<ResourceLocation, Callable<InputStream>> resources = new HashMap<>();
	private Map<ResourceLocation, ResourceLocation> textures = new HashMap<>();

	public RGBBlocksPack() {
		super(new File("rgbblocks_virtual_pack"));
		this.packInfo = new PackMetadataSection(new TranslationTextComponent("rgbblocks.pack_description"), 6);
		fillTexturesMap();
	}

	private void fillTexturesMap() {
		Map<String, String> map = new HashMap<>();

		map.put("concrete_powder", "white_concrete_powder");
		map.put("concrete", "white_concrete");
		map.put("glass_pane_top", "white_stained_glass_pane_top");
		map.put("glass", "white_stained_glass");
		map.put("glowstone", "glowstone");
		map.put("planks", "birch_planks");
		map.put("redstone_lamp_on", "redstone_lamp_on");
		map.put("redstone_lamp", "redstone_lamp");
		map.put("terracotta", "white_terracotta");
		map.put("wool", "white_wool");

		map.forEach((modName, vanillaName) -> textures.put(new ResourceLocation(RGBBlocks.MOD_ID, BLOCK_DIRECTORY + modName), new ResourceLocation(BLOCK_DIRECTORY + vanillaName)));
	}

	@Override
	public CompletableFuture<Void> reload(IStage stage, IResourceManager manager, IProfiler workerProfiler, IProfiler mainProfiler, Executor workerExecutor, Executor mainExecutor) {
		this.gatherTextureData(manager, mainProfiler);
		return CompletableFuture.supplyAsync(() -> null, workerExecutor).thenCompose(stage::wait).thenAcceptAsync((noResult) -> {
		}, mainExecutor);
	}

	protected void gatherTextureData(IResourceManager manager, IProfiler profiler) {
		Map<ResourceLocation, Callable<InputStream>> resourceStreams = new HashMap<>();

		textures.forEach((modLocation, vanillaLocation) -> {
			generateImage(modLocation, vanillaLocation, Minecraft.getInstance().getResourceManager()).ifPresent(pair -> {
				NativeImage image = pair.getFirst();
				ResourceLocation textureID = makeTextureID(modLocation);
				resourceStreams.put(textureID, () -> new ByteArrayInputStream(image.asByteArray()));
				pair.getSecond().ifPresent(metadataGetter -> resourceStreams.put(getMetadataLocation(textureID), metadataGetter));
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

	public Optional<Pair<NativeImage, Optional<Callable<InputStream>>>> generateImage(ResourceLocation modLocation, ResourceLocation vanillaLocation, IResourceManager manager) {
		ResourceLocation parentFile = makeTextureID(vanillaLocation);
		try (InputStream inputStream = manager.getResource(parentFile).getInputStream()) {
			NativeImage image = NativeImage.read(inputStream);
			NativeImage transformedImage = this.transformImage(image);
			ResourceLocation metadata = getMetadataLocation(parentFile);
			Optional<Callable<InputStream>> metadataLookup = Optional.empty();
			if (manager.hasResource(metadata)) {
				BufferedReader bufferedReader = null;
				JsonObject metadataJson = null;
				try (InputStream metadataStream = manager.getResource(metadata).getInputStream()) {
					bufferedReader = new BufferedReader(new InputStreamReader(metadataStream, StandardCharsets.UTF_8));
					metadataJson = JSONUtils.parse(bufferedReader);
				} finally {
					IOUtils.closeQuietly(bufferedReader);
				}
				if (metadataJson != null) {
					JsonObject metaDataJsonForLambda = metadataJson;
					metadataLookup = Optional.of(() -> new ByteArrayInputStream(metaDataJsonForLambda.toString().getBytes()));
				}
			}
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
				image.setPixelRGBA(x, y, ((oldColor >> 24) & 0xFF) << 24 | ((newColor >> 0) & 0xFF) << 16 | ((newColor >> 8) & 0xFF) << 8 | ((newColor >> 16) & 0xFF) << 0);
			}
		}
		return image;
	}

	@Override
	public String getName() {
		return new TranslationTextComponent("rgbblocks.pack_title").getString();
	}

	@SuppressWarnings("unchecked") @Override
	public <T> T getMetadataSection(IMetadataSectionSerializer<T> serializer) throws IOException {
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
	public Set<String> getNamespaces(ResourcePackType type) {
		return NAMESPACES;
	}

	@Override
	public boolean hasResource(ResourcePackType type, ResourceLocation id) {
		return type == ResourcePackType.CLIENT_RESOURCES && (this.resources.containsKey(id));
	}

	@Override
	public InputStream getResource(ResourcePackType type, ResourceLocation id) throws IOException {
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

	public ResourcePackFileNotFoundException makeFileNotFoundException(ResourcePackType type, ResourceLocation id) {
		String path = String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
		return new ResourcePackFileNotFoundException(this.file, path);
	}

	@Override
	public Collection<ResourceLocation> getResources(ResourcePackType type, String namespace, String id, int maxDepth, Predicate<String> filter) {
		return NO_RESOURCES;
	}

}
