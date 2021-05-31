package platinpython.rgbblocks.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

public class ModLanguageProvider extends LanguageProvider {
	public ModLanguageProvider(DataGenerator generator) {
		super(generator, RGBBlocks.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.rgbBlocks", "RGB Blocks");
		
		add(ItemRegistry.BUCKET_OF_PAINT.get(), "Bucket of Paint");
		
		add(BlockRegistry.RGB_CARPET.get(), "RGB Carpet");
		add(BlockRegistry.RGB_CONCRETE.get(), "RGB Concrete");
		add(BlockRegistry.RGB_CONCRETE_POWDER.get(), "RGB Concrete Powder");
		add(BlockRegistry.RGB_CONCRETE_SLAB.get(), "RGB Concrete Slab");
		add(BlockRegistry.RGB_CONCRETE_STAIRS.get(), "RGB Concrete Stairs");
		add(BlockRegistry.RGB_GLASS.get(), "RGB Glass");
		add(BlockRegistry.RGB_GLASS_SLAB.get(), "RGB Glass Slab");
		add(BlockRegistry.RGB_GLASS_STAIRS.get(), "RGB Glass Stairs");
		add(BlockRegistry.RGB_PLANKS.get(), "RGB Planks");
		add(BlockRegistry.RGB_PLANKS_SLAB.get(), "RGB Plank Slab");
		add(BlockRegistry.RGB_PLANKS_STAIRS.get(), "RGB Plank Stairs");
		add(BlockRegistry.RGB_TERRACOTTA.get(), "RGB Terracotta");
		add(BlockRegistry.RGB_TERRACOTTA_SLAB.get(), "RGB Terracotta Slab");
		add(BlockRegistry.RGB_TERRACOTTA_STAIRS.get(), "RGB Terracotta Stairs");
		add(BlockRegistry.RGB_WOOL.get(), "RGB Wool");
		add(BlockRegistry.RGB_WOOL_SLAB.get(), "RGB Wool Slab");
		add(BlockRegistry.RGB_WOOL_STAIRS.get(), "RGB Wool Stairs");
		
		addGui("useRGB", "Use RGB");
		addGui("useHSB", "Use HSB");
		addGui("red", "Red");
		addGui("green", "Green");
		addGui("blue", "Blue");
		addGui("hue", "Hue");
		addGui("saturation", "Saturation");
		addGui("brightness", "Brightness");
	}
	
	private void addGui(String suffix, String text) {
		add("gui." + RGBBlocks.MOD_ID + "." + suffix, text);
	}
	
	@Override
	public String getName() {
		return RGBBlocks.MOD_ID +  "_lang";
	}
}
