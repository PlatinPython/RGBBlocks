package platinpython.rgbblocks.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.registries.RegistryObject;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.colorhandlers.PaintBucketItemColor;
import platinpython.rgbblocks.client.colorhandlers.RGBBlockColor;
import platinpython.rgbblocks.client.colorhandlers.RGBBlockItemColor;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.client.model.AntiblockBakedModel;
import platinpython.rgbblocks.client.renderer.entity.RGBFallingBlockRenderer;
import platinpython.rgbblocks.util.pack.RGBBlocksPack;
import platinpython.rgbblocks.util.registries.EntityRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

@EventBusSubscriber(modid = RGBBlocks.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientUtils {
    public static final RGBBlocksPack VIRTUAL_PACK = new RGBBlocksPack();

    @SubscribeEvent
    public static void onModConstruct(FMLConstructModEvent event) {
        event.enqueueWork(ClientUtils::registerVirtualPack);
    }

    private static void registerVirtualPack() {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.getResourcePackRepository()
                 .addPackFinder((infoConsumer, packInfo) -> infoConsumer.accept(
                         new Pack("rgbblocks_textures", true, () -> VIRTUAL_PACK,
                                  Component.translatable("rgbblocks.pack_title"),
                                  Component.translatable("rgbblocks.pack_description"), PackCompatibility.COMPATIBLE,
                                  Pack.Position.TOP, true, PackSource.DEFAULT, true
                         )));

        ReloadableResourceManager resourceManager = (ReloadableResourceManager) minecraft.getResourceManager();
        resourceManager.registerReloadListener(VIRTUAL_PACK);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.RGB_FALLING_BLOCK.get(), RGBFallingBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.getItemColors()
             .register(new RGBBlockItemColor(),
                       RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new)
             );

        event.getItemColors().register(new PaintBucketItemColor(), ItemRegistry.PAINT_BUCKET.get());
    }

    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.getBlockColors()
             .register(new RGBBlockColor(),
                       RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new)
             );
    }

    @SubscribeEvent
    public static void registerModelStuff(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(RGBBlocks.MOD_ID, "antiblock_model"),
                                           new AntiblockBakedModel.ModelLoader()
        );
    }

    public static void openColorSelectScreen(int color, boolean isRGBSelected) {
        Minecraft.getInstance().setScreen(new ColorSelectScreen(color, isRGBSelected));
    }

    public static boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }
}