package platinpython.rgbblocks.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.color.RGBBlockTintSource;
import platinpython.rgbblocks.client.color.RGBItemTintSource;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.client.renderer.entity.RGBFallingBlockRenderer;
import platinpython.rgbblocks.util.pack.RGBBlocksPack;
import platinpython.rgbblocks.util.registries.EntityRegistry;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = RGBBlocks.MOD_ID, value = Dist.CLIENT)
public class ClientUtils {
    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        event.addRepositorySource(
            infoConsumer -> infoConsumer
                .accept(Pack.readMetaAndCreate(RGBBlocksPack.LOCATION_INFO, new Pack.ResourcesSupplier() {
                    final PackResources PACK = new RGBBlocksPack();

                    @Override
                    public PackResources openPrimary(PackLocationInfo location) {
                        return PACK;
                    }

                    @Override
                    public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                        return PACK;
                    }
                }, PackType.CLIENT_RESOURCES, new PackSelectionConfig(true, Pack.Position.TOP, false)))
        );
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.RGB_FALLING_BLOCK.get(), RGBFallingBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "color"), RGBItemTintSource.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(
            List.of(new RGBBlockTintSource()),
            RegistryHandler.BLOCKS.getEntries().stream().map(Supplier::get).toArray(Block[]::new)
        );
    }

    // @SubscribeEvent
    // public static void registerModelStuff(ModelEvent.RegisterGeometryLoaders event) {
    // event.register(
    // ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "antiblock_model"),
    // new AntiblockBakedModel.ModelLoader()
    // );
    // }

    public static void openColorSelectScreen(int color, boolean isRGBSelected) {
        Minecraft.getInstance().setScreen(new ColorSelectScreen(color, isRGBSelected));
    }
}
