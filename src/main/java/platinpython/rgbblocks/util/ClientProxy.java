package platinpython.rgbblocks.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.colorhandlers.PaintBucketItemColor;
import platinpython.rgbblocks.client.colorhandlers.RGBBlockColor;
import platinpython.rgbblocks.client.colorhandlers.RGBBlockItemColor;
import platinpython.rgbblocks.client.gui.PaintBucketScreen;
import platinpython.rgbblocks.util.pack.RGBBlocksPack;
import platinpython.rgbblocks.util.registries.ItemRegistry;

@EventBusSubscriber(modid = RGBBlocks.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientProxy {
	public static final RGBBlocksPack VIRTUAL_PACK = new RGBBlocksPack();

	@SubscribeEvent
	public static void onModConstruct(FMLConstructModEvent event) {
		event.enqueueWork(ClientProxy::registerVirtualPack);
	}

	private static void registerVirtualPack() {
		Minecraft minecraft = Minecraft.getInstance();

		minecraft.getResourcePackRepository().addPackFinder((infoConsumer, packInfo) -> infoConsumer.accept(new ResourcePackInfo("rgbblocks_textures", true, () -> VIRTUAL_PACK, new TranslationTextComponent("rgbblocks.pack_title"), new TranslationTextComponent("rgbblocks.pack_description"), PackCompatibility.COMPATIBLE, ResourcePackInfo.Priority.TOP, true, IPackNameDecorator.DEFAULT, true)));

		IReloadableResourceManager resourceManager = (IReloadableResourceManager) minecraft.getResourceManager();
		resourceManager.registerReloadListener(VIRTUAL_PACK);
	}

	@SubscribeEvent
	public static void registerColorHandlers(ColorHandlerEvent.Item event) {
		event.getBlockColors().register(new RGBBlockColor(), RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new));
		event.getItemColors().register(new RGBBlockItemColor(), RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new));

		event.getItemColors().register(new PaintBucketItemColor(), ItemRegistry.PAINT_BUCKET.get());
	}

	public static void openPaintbucketGUI(int color, boolean isRGBSelected) {
		Minecraft.getInstance().setScreen(new PaintBucketScreen(color, isRGBSelected));
	}
}