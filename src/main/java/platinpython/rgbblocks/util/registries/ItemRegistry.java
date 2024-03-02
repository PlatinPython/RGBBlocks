package platinpython.rgbblocks.util.registries;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredItem;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class ItemRegistry {
    public static final DeferredItem<PaintBucketItem> PAINT_BUCKET =
        RegistryHandler.ITEMS.register("paint_bucket", PaintBucketItem::new);

    public static void register() {
        RegistryHandler.ITEMS.addAlias(new ResourceLocation(RGBBlocks.MOD_ID, "bucket_of_paint"), PAINT_BUCKET.getId());
    }
}
