package platinpython.rgbblocks.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.rgbblocks.RGBBlocks;

public class ModItemModelProvider extends ItemModelProvider {
	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, RGBBlocks.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
	}
}
