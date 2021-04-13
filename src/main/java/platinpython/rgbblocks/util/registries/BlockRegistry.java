package platinpython.rgbblocks.util.registries;

import java.util.ArrayList;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.Blocks;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.block.GenericRGBBlock;
import platinpython.rgbblocks.block.GenericRGBSlabBlock;
import platinpython.rgbblocks.block.GenericRGBStairsBlock;
import platinpython.rgbblocks.block.RGBCarpetBlock;
import platinpython.rgbblocks.block.RGBConcretePowderBlock;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class BlockRegistry {
	public static final RegistryObject<Block> RGB_CONCRETE = register("concrete",
			() -> new GenericRGBBlock(Properties.from(Blocks.WHITE_CONCRETE)), false);
	public static final RegistryObject<Block> RGB_CONCRETE_STAIRS = register("concrete_stairs",
			() -> new GenericRGBStairsBlock(() -> RGB_CONCRETE.get().getDefaultState(),
					Properties.from(Blocks.WHITE_CONCRETE)),
			false);
	public static final RegistryObject<Block> RGB_CONCRETE_SLAB = register("concrete_slab",
			() -> new GenericRGBSlabBlock(Properties.from(Blocks.WHITE_CONCRETE)), false);
	
	public static final RegistryObject<Block> RGB_CONCRETE_POWDER = register("concrete_powder",
			RGBConcretePowderBlock::new, false);

	public static final RegistryObject<Block> RGB_WOOL = register("wool",
			() -> new GenericRGBBlock(Properties.from(Blocks.WHITE_WOOL)), false);
	public static final RegistryObject<Block> RGB_WOOL_STAIRS = register("wool_stairs",
			() -> new GenericRGBStairsBlock(() -> RGB_WOOL.get().getDefaultState(), Properties.from(Blocks.WHITE_WOOL)),
			false);
	public static final RegistryObject<Block> RGB_WOOL_SLAB = register("wool_slab",
			() -> new GenericRGBSlabBlock(Properties.from(Blocks.WHITE_WOOL)), false);
	
	public static final RegistryObject<Block> RGB_CARPET = register("carpet", RGBCarpetBlock::new, false);

	public static final RegistryObject<Block> RGB_PLANKS = register("planks",
			() -> new GenericRGBBlock(Properties.from(Blocks.BIRCH_PLANKS)), false);
	public static final RegistryObject<Block> RGB_PLANKS_STAIRS = register("planks_stairs",
			() -> new GenericRGBStairsBlock(() -> RGB_PLANKS.get().getDefaultState(),
					Properties.from(Blocks.BIRCH_PLANKS)),
			false);
	public static final RegistryObject<Block> RGB_PLANKS_SLAB = register("planks_slab",
			() -> new GenericRGBSlabBlock(Properties.from(Blocks.BIRCH_PLANKS)), false);

	public static final RegistryObject<Block> RGB_TERRACOTTA = register("terracotta",
			() -> new GenericRGBBlock(Properties.from(Blocks.TERRACOTTA)), false);
	public static final RegistryObject<Block> RGB_TERRACOTTA_STAIRS = register("terracotta_stairs",
			() -> new GenericRGBStairsBlock(() -> RGB_TERRACOTTA.get().getDefaultState(),
					Properties.from(Blocks.TERRACOTTA)),
			false);
	public static final RegistryObject<Block> RGB_TERRACOTTA_SLAB = register("terracotta_slab",
			() -> new GenericRGBSlabBlock(Properties.from(Blocks.TERRACOTTA)), false);

	public static final RegistryObject<Block> RGB_GLASS = register("glass",
			() -> new GenericRGBBlock(Properties.from(Blocks.GLASS)), false);
	public static final RegistryObject<Block> RGB_GLASS_STAIRS = register("glass_stairs",
			() -> new GenericRGBStairsBlock(() -> RGB_GLASS.get().getDefaultState(), Properties.from(Blocks.GLASS)),
			false);
	public static final RegistryObject<Block> RGB_GLASS_SLAB = register("glass_slab",
			() -> new GenericRGBSlabBlock(Properties.from(Blocks.GLASS)), false);

	public static ArrayList<Block> nonGlowingBlocks = new ArrayList<>();

	public static void register() {
	}

	private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
		return RegistryHandler.BLOCKS.register(name, block);
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, boolean isGlowing) {
		RegistryObject<T> ret = registerNoItem(name, block);
		ret.ifPresent(obj -> {
			if (!isGlowing)
				nonGlowingBlocks.add(ret.get());
		});
		RegistryHandler.ITEMS.register(name, () -> new RGBBlockItem(ret.get()));
		return ret;
	}
}
