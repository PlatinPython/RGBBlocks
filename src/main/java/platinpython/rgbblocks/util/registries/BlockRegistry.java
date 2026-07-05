package platinpython.rgbblocks.util.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import platinpython.rgbblocks.block.RGBBlock;
import platinpython.rgbblocks.block.RGBCarpetBlock;
import platinpython.rgbblocks.block.RGBConcreteBlock;
import platinpython.rgbblocks.block.RGBConcretePowderBlock;
import platinpython.rgbblocks.block.RGBGlassBlock;
import platinpython.rgbblocks.block.RGBGlassPaneBlock;
import platinpython.rgbblocks.block.RGBGlassSlabBlock;
import platinpython.rgbblocks.block.RGBGlassStairsBlock;
import platinpython.rgbblocks.block.RGBRedstoneLampBlock;
import platinpython.rgbblocks.block.RGBSlabBlock;
import platinpython.rgbblocks.block.RGBStairsBlock;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.RegistryHandler;

import java.util.function.Function;

public class BlockRegistry {
    public static final DeferredBlock<RGBBlock> RGB_CONCRETE =
        register("concrete", RGBConcreteBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE));
    public static final DeferredBlock<RGBSlabBlock> RGB_CONCRETE_SLAB =
        register("concrete_slab", RGBSlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE));
    public static final DeferredBlock<RGBStairsBlock> RGB_CONCRETE_STAIRS = register(
        "concrete_stairs", properties -> new RGBStairsBlock(RGB_CONCRETE.get().defaultBlockState(), properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)
    );

    public static final DeferredBlock<RGBConcretePowderBlock> RGB_CONCRETE_POWDER = register(
        "concrete_powder", RGBConcretePowderBlock::new,
        BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE_POWDER)
    );

    public static final DeferredBlock<RGBBlock> RGB_WOOL =
        register("wool", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL));
    public static final DeferredBlock<RGBSlabBlock> RGB_WOOL_SLAB =
        register("wool_slab", RGBSlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL));
    public static final DeferredBlock<RGBStairsBlock> RGB_WOOL_STAIRS = register(
        "wool_stairs", properties -> new RGBStairsBlock(RGB_WOOL.get().defaultBlockState(), properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
    );

    public static final DeferredBlock<RGBCarpetBlock> RGB_CARPET =
        register("carpet", RGBCarpetBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CARPET));

    public static final DeferredBlock<RGBBlock> RGB_PLANKS =
        register("planks", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS));
    public static final DeferredBlock<RGBSlabBlock> RGB_PLANKS_SLAB =
        register("planks_slab", RGBSlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS));
    public static final DeferredBlock<RGBStairsBlock> RGB_PLANKS_STAIRS = register(
        "planks_stairs", properties -> new RGBStairsBlock(RGB_PLANKS.get().defaultBlockState(), properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS)
    );

    public static final DeferredBlock<RGBBlock> RGB_TERRACOTTA =
        register("terracotta", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA));
    public static final DeferredBlock<RGBSlabBlock> RGB_TERRACOTTA_SLAB =
        register("terracotta_slab", RGBSlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA));
    public static final DeferredBlock<RGBStairsBlock> RGB_TERRACOTTA_STAIRS = register(
        "terracotta_stairs", properties -> new RGBStairsBlock(RGB_TERRACOTTA.get().defaultBlockState(), properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA)
    );

    public static final DeferredBlock<RGBGlassBlock> RGB_GLASS =
        register("glass", RGBGlassBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    public static final DeferredBlock<RGBGlassSlabBlock> RGB_GLASS_SLAB =
        register("glass_slab", RGBGlassSlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    public static final DeferredBlock<RGBGlassStairsBlock> RGB_GLASS_STAIRS =
        register("glass_stairs", RGBGlassStairsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));

    public static final DeferredBlock<IronBarsBlock> RGB_GLASS_PANE =
        register("glass_pane", RGBGlassPaneBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));

    public static final DeferredBlock<RGBBlock> RGB_ANTIBLOCK =
        register("antiblock", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));

    public static final DeferredBlock<RGBBlock> RGB_GLOWSTONE =
        register("glowstone", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE));

    public static final DeferredBlock<RGBRedstoneLampBlock> RGB_REDSTONE_LAMP = register(
        "redstone_lamp", RGBRedstoneLampBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_LAMP)
    );

    public static final DeferredBlock<RGBBlock> RGB_PRISMARINE =
        register("prismarine", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE));
    public static final DeferredBlock<RGBSlabBlock> RGB_PRISMARINE_SLAB =
        register("prismarine_slab", RGBSlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_SLAB));
    public static final DeferredBlock<RGBStairsBlock> RGB_PRISMARINE_STAIRS = register(
        "prismarine_stairs", properties -> new RGBStairsBlock(RGB_PRISMARINE.get().defaultBlockState(), properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_STAIRS)
    );

    public static final DeferredBlock<RGBBlock> RGB_PRISMARINE_BRICKS =
        register("prismarine_bricks", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS));
    public static final DeferredBlock<RGBSlabBlock> RGB_PRISMARINE_BRICK_SLAB = register(
        "prismarine_bricks_slab", RGBSlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICK_SLAB)
    );
    public static final DeferredBlock<RGBStairsBlock> RGB_PRISMARINE_BRICK_STAIRS = register(
        "prismarine_bricks_stairs",
        properties -> new RGBStairsBlock(RGB_PRISMARINE_BRICKS.get().defaultBlockState(), properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICK_STAIRS)
    );

    public static final DeferredBlock<RGBBlock> RGB_DARK_PRISMARINE =
        register("dark_prismarine", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE));
    public static final DeferredBlock<RGBSlabBlock> RGB_DARK_PRISMARINE_SLAB = register(
        "dark_prismarine_slab", RGBSlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE_SLAB)
    );
    public static final DeferredBlock<RGBStairsBlock> RGB_DARK_PRISMARINE_STAIRS = register(
        "dark_prismarine_stairs",
        properties -> new RGBStairsBlock(RGB_DARK_PRISMARINE.get().defaultBlockState(), properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE_STAIRS)
    );

    public static final DeferredBlock<RGBBlock> RGB_SEA_LANTERN =
        register("sea_lantern", RGBBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SEA_LANTERN));

    public static void register() {}

    private static <T extends Block> DeferredBlock<T> register(
        String name,
        Function<BlockBehaviour.Properties, T> factory,
        BlockBehaviour.Properties properties
    ) {
        DeferredBlock<T> ret = RegistryHandler.BLOCKS.registerBlock(name, factory, () -> properties);
        RegistryHandler.ITEMS.registerItem(
            name, itemProperties -> new RGBBlockItem(ret.get(), itemProperties),
            () -> new Item.Properties().useBlockDescriptionPrefix().component(DataComponentRegistry.COLOR, -1)
        );
        return ret;
    }
}
