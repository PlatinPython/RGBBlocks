package platinpython.rgbblocks.util.registries;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.block.RGBFlatBlock;
import platinpython.rgbblocks.block.RGBFlatSlabBlock;
import platinpython.rgbblocks.block.RGBFlatStairsBlock;
import platinpython.rgbblocks.block.RGBGlassFlatBlock;
import platinpython.rgbblocks.block.RGBGlassFlatSlabBlock;
import platinpython.rgbblocks.block.RGBGlassFlatStairsBlock;
import platinpython.rgbblocks.block.RGBLampFlatBlock;
import platinpython.rgbblocks.block.RGBLampFlatSlabBlock;
import platinpython.rgbblocks.block.RGBLampFlatStairsBlock;
import platinpython.rgbblocks.block.RGBLampGlassFlatBlock;
import platinpython.rgbblocks.block.RGBLampGlassFlatSlabBlock;
import platinpython.rgbblocks.block.RGBLampGlassFlatStairsBlock;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class BlockRegistry {
	public static final RegistryObject<Block> RGB_FLAT = registerNoItem("rgb_flat", RGBFlatBlock::new);
	public static final RegistryObject<Block> RGB_FLAT_SLAB = registerNoItem("rgb_flat_slab", RGBFlatSlabBlock::new);
	public static final RegistryObject<Block> RGB_FLAT_STAIRS = registerNoItem("rgb_flat_stairs",
			RGBFlatStairsBlock::new);

	public static final RegistryObject<Block> RGB_GLASS_FLAT = registerNoItem("rgb_glass_flat", RGBGlassFlatBlock::new);
	public static final RegistryObject<Block> RGB_GLASS_FLAT_SLAB = registerNoItem("rgb_glass_flat_slab",
			RGBGlassFlatSlabBlock::new);
	public static final RegistryObject<Block> RGB_GLASS_FLAT_STAIRS = registerNoItem("rgb_glass_flat_stairs",
			RGBGlassFlatStairsBlock::new);

	public static final RegistryObject<Block> RGB_LAMP_FLAT = registerNoItem("rgb_lamp_flat", RGBLampFlatBlock::new);
	public static final RegistryObject<Block> RGB_LAMP_FLAT_SLAB = registerNoItem("rgb_lamp_flat_slab",
			RGBLampFlatSlabBlock::new);
	public static final RegistryObject<Block> RGB_LAMP_FLAT_STAIRS = registerNoItem("rgb_lamp_flat_stairs",
			RGBLampFlatStairsBlock::new);

	public static final RegistryObject<Block> RGB_LAMP_GLASS_FLAT = registerNoItem("rgb_lamp_glass_flat",
			RGBLampGlassFlatBlock::new);
	public static final RegistryObject<Block> RGB_LAMP_GLASS_FLAT_SLAB = registerNoItem("rgb_lamp_glass_flat_slab",
			RGBLampGlassFlatSlabBlock::new);
	public static final RegistryObject<Block> RGB_LAMP_GLASS_FLAT_STAIRS = registerNoItem("rgb_lamp_glass_flat_stairs",
			RGBLampGlassFlatStairsBlock::new);

	public static void register() {
	}

	private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
		return RegistryHandler.BLOCKS.register(name, block);
	}

	@SuppressWarnings("unused")
	private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
		RegistryObject<T> ret = registerNoItem(name, block);
		RegistryHandler.ITEMS.register(name, () -> new RGBBlockItem(ret.get()));
		return ret;
	}
}
