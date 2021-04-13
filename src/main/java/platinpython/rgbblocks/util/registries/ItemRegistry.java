package platinpython.rgbblocks.util.registries;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.item.PaintbucketItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class ItemRegistry {
	// Items
	public static final RegistryObject<Item> BUCKET_OF_PAINT = RegistryHandler.ITEMS.register("bucket_of_paint",
			PaintbucketItem::new);

	public static void register() {
	}
}
