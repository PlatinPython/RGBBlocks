package platinpython.rgbblocks.util.client.colorhandlers;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import platinpython.rgbblocks.item.PaintBucketItem;

public class PaintBucketItemColor implements IItemColor {
	@Override
	public int getColor(ItemStack stack, int tintindex) {
		if (stack.getItem() instanceof PaintBucketItem) {
			if (!stack.hasTag() || !stack.getTag().contains("color")) {
				return 0;
			}
			if (tintindex == 1) {
				return stack.getTag().getInt("color");
			} else {
				return -1;
			}
		} else {
			return 0;
		}
	}
}