package platinpython.rgbblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import platinpython.rgbblocks.client.gui.PaintBucketScreen;

public class ClientHandler {
	public static void openPaintbucketGUI(ItemStack stack) {
		Minecraft.getInstance().setScreen(new PaintBucketScreen(stack.getTag().getInt("color"), stack.getTag().getBoolean("isRGBSelected")));
	}
}
