package platinpython.rgbblocks.util.registries.client;

import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.client.colorhandlers.PaintBucketItemColor;
import platinpython.rgbblocks.util.client.colorhandlers.RGBBlockColor;
import platinpython.rgbblocks.util.client.colorhandlers.RGBBlockItemColor;
import platinpython.rgbblocks.util.registries.ItemRegistry;

@EventBusSubscriber(modid = RGBBlocks.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientRegistry {
	@SubscribeEvent
	public static void registerColorHandlers(ColorHandlerEvent.Item event) {
		RGBBlocks.LOGGER.debug("Registering ColorHandlers");
		event.getBlockColors().register(new RGBBlockColor(),
				RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new));
		event.getItemColors().register(new RGBBlockItemColor(),
				RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new));

		event.getItemColors().register(new PaintBucketItemColor(), ItemRegistry.PAINT_BUCKET.get());
	}
}