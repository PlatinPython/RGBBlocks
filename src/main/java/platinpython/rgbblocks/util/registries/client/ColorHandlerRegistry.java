package platinpython.rgbblocks.util.registries.client;

import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.client.colorhandlers.PaintbucketItemColor;
import platinpython.rgbblocks.util.client.colorhandlers.RGBBlockColor;
import platinpython.rgbblocks.util.client.colorhandlers.RGBBlockItemColor;
import platinpython.rgbblocks.util.registries.ItemRegistry;

@EventBusSubscriber(modid = RGBBlocks.MOD_ID, value = Dist.CLIENT)
public class ColorHandlerRegistry {
	@SubscribeEvent
	public void registerRGBColors(ColorHandlerEvent.Item event) {
		event.getBlockColors().register(new RGBBlockColor(),
				RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new));
		event.getItemColors().register(new RGBBlockItemColor(),
				RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new));
		
		event.getItemColors().register(new PaintbucketItemColor(), ItemRegistry.BUCKET_OF_PAINT.get());
	}
}
