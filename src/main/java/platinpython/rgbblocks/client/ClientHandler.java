package platinpython.rgbblocks.client;

import net.minecraft.item.ItemStack;
import platinpython.rgbblocks.client.gui.PaintbucketScreen;

public class ClientHandler {
	public static void openGUI(ItemStack stack) {
		new PaintbucketScreen(stack.getTag().getInt("color"));
	}
}
