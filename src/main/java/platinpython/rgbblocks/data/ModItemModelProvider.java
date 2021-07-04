package platinpython.rgbblocks.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.registries.ItemRegistry;

public class ModItemModelProvider extends ItemModelProvider {
	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, RGBBlocks.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		item2Layers(ItemRegistry.PAINT_BUCKET.get());
	}

	private void item2Layers(Item item) {
		String path = item.getRegistryName().getPath();
		String loc = ITEM_FOLDER + "/" + path;
		singleTexture(path, mcLoc(ITEM_FOLDER + "/generated"), "layer0", modLoc(loc)).texture("layer1", modLoc(loc + "_color"));
	}
}
