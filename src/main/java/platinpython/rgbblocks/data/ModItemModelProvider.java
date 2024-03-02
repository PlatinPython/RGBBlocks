package platinpython.rgbblocks.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RGBBlocks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        item2Layers(ItemRegistry.PAINT_BUCKET);

        singleTexture(
            BlockRegistry.RGB_GLASS_PANE.getId().getPath(), mcLoc(ITEM_FOLDER + "/generated"), "layer0",
            modLoc(BLOCK_FOLDER + "/glass")
        ).renderType("translucent");
    }

    private void item2Layers(@SuppressWarnings("SameParameterValue") DeferredItem<? extends Item> item) {
        String path = item.getId().getPath();
        String loc = ITEM_FOLDER + "/" + path;
        singleTexture(path, mcLoc(ITEM_FOLDER + "/generated"), "layer0", modLoc(loc))
            .texture("layer1", modLoc(loc + "_color"));
    }
}
