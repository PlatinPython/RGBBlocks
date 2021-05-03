package platinpython.rgbblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import platinpython.rgbblocks.client.gui.PaintbucketScreen;

public class ClientHandler {
	public static void openPaintbucketGUI(ItemStack stack) {
		Minecraft.getInstance().setScreen(new PaintbucketScreen(stack.getTag().getInt("color")));
	}
}
