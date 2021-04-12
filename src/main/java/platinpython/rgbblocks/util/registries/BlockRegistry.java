package platinpython.rgbblocks.util.registries;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.block.GenericRGBBlock;
import platinpython.rgbblocks.block.GenericRGBGlassBlock;
import platinpython.rgbblocks.block.GenericRGBGlassSlabBlock;
import platinpython.rgbblocks.block.GenericRGBGlassStairsBlock;
import platinpython.rgbblocks.block.GenericRGBSlabBlock;
import platinpython.rgbblocks.block.GenericRGBStairsBlock;
import platinpython.rgbblocks.block.RGBLampFlatBlock;
import platinpython.rgbblocks.block.RGBLampFlatSlabBlock;
import platinpython.rgbblocks.block.RGBLampFlatStairsBlock;
import platinpython.rgbblocks.block.RGBLampGlassFlatBlock;
import platinpython.rgbblocks.block.RGBLampGlassFlatSlabBlock;
import platinpython.rgbblocks.block.RGBLampGlassFlatStairsBlock;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class BlockRegistry {
	private static final Properties rock = Properties.create(Material.ROCK);
	private static final Properties glass = Properties.create(Material.GLASS).sound(SoundType.GLASS).notSolid();

	public static final RegistryObject<Block> RGB_FLAT = register("rgb_flat", () -> new GenericRGBBlock(rock));
	public static final RegistryObject<Block> RGB_FLAT_SLAB = register("rgb_flat_slab",
			() -> new GenericRGBSlabBlock(rock));
	public static final RegistryObject<Block> RGB_FLAT_STAIRS = register("rgb_flat_stairs",
			() -> new GenericRGBStairsBlock(() -> RGB_FLAT.get().getDefaultState(), rock));

	public static final RegistryObject<Block> RGB_GLASS_FLAT = register("rgb_glass_flat",
			() -> new GenericRGBGlassBlock(glass));
	public static final RegistryObject<Block> RGB_GLASS_FLAT_SLAB = register("rgb_glass_flat_slab",
			() -> new GenericRGBGlassSlabBlock(glass));
	public static final RegistryObject<Block> RGB_GLASS_FLAT_STAIRS = register("rgb_glass_flat_stairs",
			() -> new GenericRGBGlassStairsBlock(() -> RGB_GLASS_FLAT.get().getDefaultState(), glass));

	public static final RegistryObject<Block> RGB_LAMP_FLAT = register("rgb_lamp_flat", RGBLampFlatBlock::new);
	public static final RegistryObject<Block> RGB_LAMP_FLAT_SLAB = register("rgb_lamp_flat_slab",
			RGBLampFlatSlabBlock::new);
	public static final RegistryObject<Block> RGB_LAMP_FLAT_STAIRS = register("rgb_lamp_flat_stairs",
			RGBLampFlatStairsBlock::new);

	public static final RegistryObject<Block> RGB_LAMP_GLASS_FLAT = register("rgb_lamp_glass_flat",
			RGBLampGlassFlatBlock::new);
	public static final RegistryObject<Block> RGB_LAMP_GLASS_FLAT_SLAB = register("rgb_lamp_glass_flat_slab",
			RGBLampGlassFlatSlabBlock::new);
	public static final RegistryObject<Block> RGB_LAMP_GLASS_FLAT_STAIRS = register("rgb_lamp_glass_flat_stairs",
			RGBLampGlassFlatStairsBlock::new);

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
