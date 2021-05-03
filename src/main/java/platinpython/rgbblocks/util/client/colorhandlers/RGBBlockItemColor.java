package platinpython.rgbblocks.util.client.colorhandlers;

import java.awt.Color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import platinpython.rgbblocks.item.RGBBlockItem;

@OnlyIn(Dist.CLIENT)
public class RGBBlockItemColor implements IItemColor {
	public int getColor(ItemStack stack, int tintindex) {
		if (stack.getItem() instanceof RGBBlockItem) {
			if (!stack.hasTag() || !stack.getTag().contains("color")) {
				return Color.WHITE.getRGB();
			}
			CompoundNBT compound = stack.getTag();
			if(compound.contains("color")) {
				return compound.getInt("color");
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
}
