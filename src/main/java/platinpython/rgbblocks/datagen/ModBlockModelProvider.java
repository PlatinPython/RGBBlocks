package platinpython.rgbblocks.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.rgbblocks.RGBBlocks;

public class ModBlockModelProvider extends BlockModelProvider {
	public ModBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, RGBBlocks.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
	}
}
