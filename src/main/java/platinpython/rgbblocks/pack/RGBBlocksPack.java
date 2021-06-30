package platinpython.rgbblocks.pack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.resources.data.PackMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import platinpython.rgbblocks.RGBBlocks;

public class RGBBlocksPack extends ResourcePack implements IFutureReloadListener {
	public static final Set<String> NAMESPACES = ImmutableSet.of(RGBBlocks.MOD_ID);
	public static final List<ResourceLocation> NO_RESOURCES = Collections.emptyList();

	private final PackMetadataSection packInfo;
	private Map<ResourceLocation, Callable<InputStream>> resources = new HashMap<>();

	public RGBBlocksPack() {
		super(new File("rgbblocks_virtual_pack"));
		this.packInfo = new PackMetadataSection(new TranslationTextComponent("rgbblocks.pack_description"), 0);
	}

	@Override
	public CompletableFuture<Void> reload(IStage stage, IResourceManager manager, IProfiler workerProfiler,
			IProfiler mainProfiler, Executor workerExecutor, Executor mainExecutor) {
		this.gatherTextureData(manager, mainProfiler);
		return CompletableFuture.supplyAsync(() -> null, workerExecutor).thenCompose(stage::wait)
				.thenAcceptAsync((noResult) -> {
				}, mainExecutor);
	}

	protected void gatherTextureData(IResourceManager manager, IProfiler profiler) {
	}

	@Override
	public String getName() {
		return new TranslationTextComponent("rgbblocks.pack_title").getString();
	}

	@SuppressWarnings("unchecked")
	@Override
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
	public Collection<ResourceLocation> getResources(ResourcePackType type, String namespace, String id, int maxDepth,
			Predicate<String> filter) {
		return NO_RESOURCES;
	}

}
