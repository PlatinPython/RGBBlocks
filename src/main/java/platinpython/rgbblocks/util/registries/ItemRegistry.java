package platinpython.rgbblocks.util.registries;

import net.minecraftforge.registries.RegistryObject;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class ItemRegistry {
    public static final RegistryObject<PaintBucketItem> PAINT_BUCKET =
        RegistryHandler.ITEMS.register("paint_bucket", PaintBucketItem::new);

    public static void register() {}
}
