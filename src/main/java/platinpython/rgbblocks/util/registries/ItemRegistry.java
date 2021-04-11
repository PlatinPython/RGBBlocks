package platinpython.rgbblocks.util.registries;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.item.PaintbucketItem;
import platinpython.rgbblocks.util.RegistryHandler;

public class ItemRegistry {
	// Items
	public static final RegistryObject<Item> PAINTBUCKET = RegistryHandler.ITEMS.register("paintbucket",
			PaintbucketItem::new);

	public static void register() {
	}
}
