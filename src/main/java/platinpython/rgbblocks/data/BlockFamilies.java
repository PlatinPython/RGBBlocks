package platinpython.rgbblocks.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import platinpython.rgbblocks.util.registries.BlockRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class BlockFamilies {
    private static final Map<Block, BlockFamily> MAP = new HashMap<>();

    // spotless:off
    public static final BlockFamily CONCRETE = familyBuilder(BlockRegistry.RGB_CONCRETE.get())
        .slab(BlockRegistry.RGB_CONCRETE_SLAB.get())
        .stairs(BlockRegistry.RGB_CONCRETE_STAIRS.get())
        .getFamily();
    public static final BlockFamily CONCRETE_POWDER = familyBuilder(BlockRegistry.RGB_CONCRETE_POWDER.get())
        .dontGenerateModel()
        .getFamily();
    public static final BlockFamily WOOL = familyBuilder(BlockRegistry.RGB_WOOL.get())
        .slab(BlockRegistry.RGB_WOOL_SLAB.get())
        .stairs(BlockRegistry.RGB_WOOL_STAIRS.get())
        .getFamily();
    public static final BlockFamily PLANKS = familyBuilder(BlockRegistry.RGB_PLANKS.get())
        .slab(BlockRegistry.RGB_PLANKS_SLAB.get())
        .stairs(BlockRegistry.RGB_PLANKS_STAIRS.get())
        .getFamily();
    public static final BlockFamily TERRACOTTA = familyBuilder(BlockRegistry.RGB_TERRACOTTA.get())
        .slab(BlockRegistry.RGB_TERRACOTTA_SLAB.get())
        .stairs(BlockRegistry.RGB_TERRACOTTA_STAIRS.get())
        .getFamily();
    public static final BlockFamily GLASS = familyBuilder(BlockRegistry.RGB_GLASS.get())
        .slab(BlockRegistry.RGB_GLASS_SLAB.get())
        .stairs(BlockRegistry.RGB_GLASS_STAIRS.get())
        .getFamily();
    public static final BlockFamily ANTIBLOCK = familyBuilder(BlockRegistry.RGB_ANTIBLOCK.get())
        .getFamily();
    public static final BlockFamily GLOWSTONE = familyBuilder(BlockRegistry.RGB_GLOWSTONE.get())
        .getFamily();
    public static final BlockFamily REDSTONE_LAMP = familyBuilder(BlockRegistry.RGB_REDSTONE_LAMP.get())
        .dontGenerateModel()
        .getFamily();
    public static final BlockFamily PRISMARINE = familyBuilder(BlockRegistry.RGB_PRISMARINE.get())
        .slab(BlockRegistry.RGB_PRISMARINE_SLAB.get())
        .stairs(BlockRegistry.RGB_PRISMARINE_STAIRS.get())
        .getFamily();
    public static final BlockFamily PRISMARINE_BRICKS = familyBuilder(BlockRegistry.RGB_PRISMARINE_BRICKS.get())
        .slab(BlockRegistry.RGB_PRISMARINE_BRICK_SLAB.get())
        .stairs(BlockRegistry.RGB_PRISMARINE_BRICK_STAIRS.get())
        .getFamily();
    public static final BlockFamily DARK_PRISMARINE = familyBuilder(BlockRegistry.RGB_DARK_PRISMARINE.get())
        .slab(BlockRegistry.RGB_DARK_PRISMARINE_SLAB.get())
        .stairs(BlockRegistry.RGB_DARK_PRISMARINE_STAIRS.get())
        .getFamily();
    public static final BlockFamily SEA_LANTERN = familyBuilder(BlockRegistry.RGB_SEA_LANTERN.get())
        .getFamily();
    // spotless:on

    private static BlockFamily.Builder familyBuilder(Block base) {
        BlockFamily.Builder builder = new BlockFamily.Builder(base);
        BlockFamily blockFamily = MAP.put(base, builder.getFamily());
        if (blockFamily != null) {
            throw new IllegalStateException("Duplicate family definition for " + BuiltInRegistries.BLOCK.getKey(base));
        } else {
            return builder;
        }
    }

    public static Stream<BlockFamily> getAllFamilies() {
        return MAP.values().stream();
    }
}
