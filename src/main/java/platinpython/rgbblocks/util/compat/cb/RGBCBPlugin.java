package platinpython.rgbblocks.util.compat.cb;

import mod.chiselsandbits.api.client.variant.state.IClientStateVariantManager;
import mod.chiselsandbits.api.plugin.ChiselsAndBitsPlugin;
import mod.chiselsandbits.api.plugin.IChiselsAndBitsPlugin;
import mod.chiselsandbits.api.variant.state.IStateVariantManager;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.registries.BlockRegistry;

import java.util.Set;

@ChiselsAndBitsPlugin
public class RGBCBPlugin implements IChiselsAndBitsPlugin {
    private static final Set<RegistryObject<? extends Block>> SUPPORTED_BLOCKS = Set.of(
        BlockRegistry.RGB_CONCRETE, BlockRegistry.RGB_CONCRETE_POWDER, BlockRegistry.RGB_WOOL, BlockRegistry.RGB_PLANKS,
        BlockRegistry.RGB_TERRACOTTA, BlockRegistry.RGB_GLASS, BlockRegistry.RGB_ANTIBLOCK, BlockRegistry.RGB_GLOWSTONE,
        BlockRegistry.RGB_REDSTONE_LAMP, BlockRegistry.RGB_PRISMARINE, BlockRegistry.RGB_PRISMARINE_BRICKS,
        BlockRegistry.RGB_DARK_PRISMARINE, BlockRegistry.RGB_SEA_LANTERN
    );

    @Override
    public String getId() {
        return RGBBlocks.MOD_ID;
    }

    @Override
    public void onConstruction() {
        SUPPORTED_BLOCKS.forEach(
            block -> IStateVariantManager.getInstance()
                .registerProvider(block::get, new RGBStateVariantProvider(block::get))
        );
    }

    @Override
    public void onClientConstruction() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Client.doConstruction();
        }
    }

    private static class Client {
        private static void doConstruction() {
            SUPPORTED_BLOCKS.forEach(
                block -> IClientStateVariantManager.getInstance()
                    .registerStateVariantProvider(block::get, new RGBClientStateVariantProvider())
            );
        }
    }
}
