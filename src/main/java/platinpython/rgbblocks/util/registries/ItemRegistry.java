package platinpython.rgbblocks.util.registries;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class ItemRegistry {
    public static final DeferredItem<PaintBucketItem> PAINT_BUCKET = RegistryHandler.ITEMS.registerItem(
        "paint_bucket", PaintBucketItem::new,
        () -> new Item.Properties().durability(500)
            .setNoCombineRepair()
            .component(DataComponentRegistry.COLOR, -1)
            .component(DataComponentRegistry.RGB_SELECTED, true)
    );

    public static void register() {
        RegistryHandler.ITEMS
            .addAlias(Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "bucket_of_paint"), PAINT_BUCKET.getId());
    }
}
