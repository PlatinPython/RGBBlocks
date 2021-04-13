package platinpython.rgbblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.client.colorhandlers.PaintbucketItemColor;
import platinpython.rgbblocks.util.client.colorhandlers.RGBBlockColor;
import platinpython.rgbblocks.util.client.colorhandlers.RGBBlockItemColor;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

@Mod("rgbblocks")
public class RGBBlocks {
	public static final String MOD_ID = "rgbblocks";

	public RGBBlocks() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		RegistryHandler.register();

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(final FMLClientSetupEvent event) {
		PacketHandler.register();

	}

	public void doClientStuff(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(BlockRegistry.RGB_GLASS.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(BlockRegistry.RGB_GLASS_STAIRS.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(BlockRegistry.RGB_GLASS_SLAB.get(), RenderType.getTranslucent());

		RGBBlockColor blockColor = new RGBBlockColor();
		RegistryHandler.BLOCKS.getEntries()
				.forEach(block -> Minecraft.getInstance().getBlockColors().register(blockColor, block.get()));

		RGBBlockItemColor blockItemColor = new RGBBlockItemColor();
		RegistryHandler.ITEMS.getEntries().forEach(item -> {
			if (item.get() instanceof RGBBlockItem)
				Minecraft.getInstance().getItemColors().register(blockItemColor, item.get());
		});

		Minecraft.getInstance().getItemColors().register(new PaintbucketItemColor(),
				ItemRegistry.BUCKET_OF_PAINT.get());
	}

	public static final ItemGroup ITEM_GROUP_RGB = new ItemGroup("rgbBlocks") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ItemRegistry.BUCKET_OF_PAINT.get());
		}
	};
}
