package platinpython.rgbblocks.util.client.colorhandlers;

import java.awt.Color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import platinpython.rgbblocks.item.PaintbucketItem;

public class PaintbucketItemColor implements IItemColor{
	@Override
	public int getColor(ItemStack stack, int tintindex) {
		if(stack.getItem() instanceof PaintbucketItem) {
			if(!stack.hasTag()) {
				return Color.WHITE.getRGB();
			}
			CompoundNBT compound = stack.getTag();
			Color color;
			color = new Color(compound.getInt("red"), compound.getInt("green"), compound.getInt("blue"));
			if(tintindex == 1) {
				return color.getRGB();
			} else {
				return Color.WHITE.getRGB();
			}
		} else {
			return Color.WHITE.getRGB();
		}
	}
}
