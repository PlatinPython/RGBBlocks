package platinpython.rgbblocks.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.rgbblocks.RGBBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
	public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, RGBBlocks.MOD_ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {		
	}
}
