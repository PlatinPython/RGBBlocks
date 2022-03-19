package platinpython.rgbblocks.util.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.fmllegacy.RegistryObject;
import platinpython.rgbblocks.block.RGBBlock;
import platinpython.rgbblocks.block.RGBCarpetBlock;
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

import java.util.function.Supplier;

public class BlockRegistry {
    public static final RegistryObject<Block> RGB_CONCRETE = register("concrete",
                                                                      () -> new RGBBlock(Properties.copy(Blocks.WHITE_CONCRETE)));
    public static final RegistryObject<Block> RGB_CONCRETE_SLAB = register("concrete_slab",
                                                                           () -> new RGBSlabBlock(Properties.copy(Blocks.WHITE_CONCRETE)));
    public static final RegistryObject<Block> RGB_CONCRETE_STAIRS = register("concrete_stairs",
                                                                             () -> new RGBStairsBlock(() -> RGB_CONCRETE.get()
                                                                                                                        .defaultBlockState(),
                                                                                                      Properties.copy(
                                                                                                              Blocks.WHITE_CONCRETE)));

    public static final RegistryObject<Block> RGB_CONCRETE_POWDER = register("concrete_powder",
                                                                             RGBConcretePowderBlock::new);

    public static final RegistryObject<Block> RGB_WOOL = register("wool",
                                                                  () -> new RGBBlock(Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> RGB_WOOL_SLAB = register("wool_slab",
                                                                       () -> new RGBSlabBlock(Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> RGB_WOOL_STAIRS = register("wool_stairs",
                                                                         () -> new RGBStairsBlock(() -> RGB_WOOL.get()
                                                                                                                .defaultBlockState(),
                                                                                                  Properties.copy(Blocks.WHITE_WOOL)));

    public static final RegistryObject<Block> RGB_CARPET = register("carpet", RGBCarpetBlock::new);

    public static final RegistryObject<Block> RGB_PLANKS = register("planks",
                                                                    () -> new RGBBlock(Properties.copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> RGB_PLANKS_SLAB = register("planks_slab",
                                                                         () -> new RGBSlabBlock(Properties.copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> RGB_PLANKS_STAIRS = register("planks_stairs",
                                                                           () -> new RGBStairsBlock(() -> RGB_PLANKS.get()
                                                                                                                    .defaultBlockState(),
                                                                                                    Properties.copy(
                                                                                                            Blocks.BIRCH_PLANKS)));

    public static final RegistryObject<Block> RGB_TERRACOTTA = register("terracotta",
                                                                        () -> new RGBBlock(Properties.copy(Blocks.TERRACOTTA)));
    public static final RegistryObject<Block> RGB_TERRACOTTA_SLAB = register("terracotta_slab",
                                                                             () -> new RGBSlabBlock(Properties.copy(
                                                                                     Blocks.TERRACOTTA)));
    public static final RegistryObject<Block> RGB_TERRACOTTA_STAIRS = register("terracotta_stairs",
                                                                               () -> new RGBStairsBlock(() -> RGB_TERRACOTTA.get()
                                                                                                                            .defaultBlockState(),
                                                                                                        Properties.copy(
                                                                                                                Blocks.TERRACOTTA)));

    public static final RegistryObject<Block> RGB_GLASS = register("glass", RGBGlassBlock::new);
    public static final RegistryObject<Block> RGB_GLASS_SLAB = register("glass_slab", RGBGlassSlabBlock::new);
    public static final RegistryObject<Block> RGB_GLASS_STAIRS = register("glass_stairs",
                                                                          () -> new RGBGlassStairsBlock(() -> RGB_GLASS.get()
                                                                                                                       .defaultBlockState()));

    public static final RegistryObject<IronBarsBlock> RGB_GLASS_PANE = register("glass_pane", RGBGlassPaneBlock::new);

    public static final RegistryObject<Block> RGB_ANTIBLOCK = register("antiblock",
                                                                       () -> new RGBBlock(Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> RGB_GLOWSTONE = register("glowstone",
                                                                       () -> new RGBBlock(Properties.copy(Blocks.GLOWSTONE)));

    public static final RegistryObject<Block> RGB_REDSTONE_LAMP = register("redstone_lamp", RGBRedstoneLampBlock::new);

    public static final RegistryObject<Block> RGB_PRISMARINE = register("prismarine",
                                                                        () -> new RGBBlock(Properties.copy(Blocks.PRISMARINE)));

    public static final RegistryObject<Block> RGB_PRISMARINE_SLAB = register("prismarine_slab",
                                                                             () -> new RGBSlabBlock(Properties.copy(
                                                                                     Blocks.PRISMARINE_SLAB)));

    public static final RegistryObject<Block> RGB_PRISMARINE_STAIRS = register("prismarine_stairs",
                                                                               () -> new RGBStairsBlock(RGB_PRISMARINE.get()::defaultBlockState,
                                                                                                        Properties.copy(
                                                                                                                Blocks.PRISMARINE_STAIRS)));


    public static final RegistryObject<Block> RGB_PRISMARINE_BRICKS = register("prismarine_bricks",
                                                                               () -> new RGBBlock(Properties.copy(Blocks.PRISMARINE_BRICKS)));

    public static final RegistryObject<Block> RGB_PRISMARINE_BRICK_SLAB = register("prismarine_bricks_slab",
                                                                                   () -> new RGBSlabBlock(Properties.copy(
                                                                                           Blocks.PRISMARINE_BRICK_SLAB)));

    public static final RegistryObject<Block> RGB_PRISMARINE_BRICK_STAIRS = register("prismarine_bricks_stairs",
                                                                                     () -> new RGBStairsBlock(
                                                                                             RGB_PRISMARINE_BRICKS.get()::defaultBlockState,
                                                                                             Properties.copy(Blocks.PRISMARINE_BRICK_STAIRS)));


    public static final RegistryObject<Block> RGB_DARK_PRISMARINE = register("dark_prismarine",
                                                                             () -> new RGBBlock(Properties.copy(Blocks.DARK_PRISMARINE)));

    public static final RegistryObject<Block> RGB_DARK_PRISMARINE_SLAB = register("dark_prismarine_slab",
                                                                                  () -> new RGBSlabBlock(Properties.copy(
                                                                                          Blocks.DARK_PRISMARINE_SLAB)));

    public static final RegistryObject<Block> RGB_DARK_PRISMARINE_STAIRS = register("dark_prismarine_stairs",
                                                                                    () -> new RGBStairsBlock(
                                                                                            RGB_DARK_PRISMARINE.get()::defaultBlockState,
                                                                                            Properties.copy(Blocks.DARK_PRISMARINE_STAIRS)));

    public static final RegistryObject<Block> RGB_SEA_LANTERN = register("sea_lantern",
                                                                         () -> new RGBBlock(Properties.copy(Blocks.SEA_LANTERN)));

    public static void register() {
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return RegistryHandler.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerNoItem(name, block);
        RegistryHandler.ITEMS.register(name, () -> new RGBBlockItem(ret.get()));
        return ret;
    }
}
