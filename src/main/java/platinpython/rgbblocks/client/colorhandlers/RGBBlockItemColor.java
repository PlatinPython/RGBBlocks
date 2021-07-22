package platinpython.rgbblocks.client.colorhandlers;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import platinpython.rgbblocks.item.RGBBlockItem;

public class RGBBlockItemColor implements ItemColor {
	public int getColor(ItemStack stack, int tintindex) {
		if (stack.getItem() instanceof RGBBlockItem) {
			if (!stack.hasTag() || !stack.getTag().contains("color")) {
				return 0;
			}
			CompoundTag compound = stack.getTag();
			if (compound.contains("color")) {
				return compound.getInt("color");
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
}
