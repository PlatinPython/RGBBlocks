package platinpython.rgbblocks.util.registries;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class ItemRegistry {
    public static final RegistryObject<Item> PAINT_BUCKET = RegistryHandler.ITEMS.register("paint_bucket",
                                                                                           PaintBucketItem::new);

    public static void register() {
    }
}
