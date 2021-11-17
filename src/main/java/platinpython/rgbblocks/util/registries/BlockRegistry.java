package platinpython.rgbblocks.util.registries;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.block.RGBBlock;
import platinpython.rgbblocks.block.RGBGlassBlock;
import platinpython.rgbblocks.block.RGBGlassSlabBlock;
import platinpython.rgbblocks.block.RGBGlassStairsBlock;
import platinpython.rgbblocks.block.RGBSlabBlock;
import platinpython.rgbblocks.block.RGBStairsBlock;
import platinpython.rgbblocks.block.RGBCarpetBlock;
import platinpython.rgbblocks.block.RGBConcretePowderBlock;
import platinpython.rgbblocks.block.RGBRedstoneLampBlock;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.RegistryHandler;

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

    public static final RegistryObject<Block> RGB_ANTIBLOCK = register("antiblock",
                                                                       () -> new RGBBlock(Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> RGB_GLOWSTONE = register("glowstone",
                                                                       () -> new RGBBlock(Properties.copy(Blocks.GLOWSTONE)));

    public static final RegistryObject<Block> RGB_REDSTONE_LAMP = register("redstone_lamp", RGBRedstoneLampBlock::new);

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
