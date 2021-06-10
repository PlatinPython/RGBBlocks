package platinpython.rgbblocks.client;

import net.minecraft.client.Minecraft;
import platinpython.rgbblocks.client.gui.PaintBucketScreen;

public class ClientHandler {
	public static void openPaintbucketGUI(int color, boolean isRGBSelected) {
		Minecraft.getInstance().setScreen(new PaintBucketScreen(color, isRGBSelected));
	}
}
