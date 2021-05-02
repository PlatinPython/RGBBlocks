package platinpython.rgbblocks.util.client.colorhandlers;

import java.awt.Color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import platinpython.rgbblocks.item.PaintbucketItem;

public class PaintbucketItemColor implements IItemColor {
	@Override
	public int getColor(ItemStack stack, int tintindex) {
		if (stack.getItem() instanceof PaintbucketItem) {
			if (!stack.hasTag() || !stack.getTag().contains("color")) {
				return Color.WHITE.getRGB();
			}
			if (tintindex == 1) {
				return stack.getTag().getInt("color");
			} else {
				return Color.WHITE.getRGB();
			}
		} else {
			return Color.WHITE.getRGB();
		}
	}
}
