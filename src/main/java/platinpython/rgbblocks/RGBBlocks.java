package platinpython.rgbblocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import platinpython.rgbblocks.client.renderer.RGBFallingBlockRenderer;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.EntityRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

@Mod("rgbblocks")
public class RGBBlocks {
	public static final String MOD_ID = "rgbblocks";

	public static final Logger LOGGER = LogManager.getLogger();

	public RGBBlocks() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		RegistryHandler.register();

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(final FMLClientSetupEvent event) {
		PacketHandler.register();

		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.RGB_FALLING_BLOCK.get(),
				RGBFallingBlockRenderer::new);
	}

	public void doClientStuff(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(BlockRegistry.RGB_GLASS.get(), RenderType.translucent());
		RenderTypeLookup.setRenderLayer(BlockRegistry.RGB_GLASS_STAIRS.get(), RenderType.translucent());
		RenderTypeLookup.setRenderLayer(BlockRegistry.RGB_GLASS_SLAB.get(), RenderType.translucent());
	}

	public static final ItemGroup ITEM_GROUP_RGB = new ItemGroup("rgbBlocks") {
		@Override
		public ItemStack makeIcon() {
			ItemStack stack = new ItemStack(ItemRegistry.BUCKET_OF_PAINT.get());
			stack.getOrCreateTag().putInt("color", -1);
			return stack;
		}
	};
}
