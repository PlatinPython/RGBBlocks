package platinpython.rgbblocks.client.colorhandlers;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import platinpython.rgbblocks.item.PaintBucketItem;

public class PaintBucketItemColor implements ItemColor {
	@Override
	public int getColor(ItemStack stack, int tintindex) {
		if (stack.getItem() instanceof PaintBucketItem) {
			if (tintindex == 1) {
				if (!stack.hasTag() || !stack.getTag().contains("color")) {
					return 0;
				} else {
					return stack.getTag().getInt("color");
				}
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
}
