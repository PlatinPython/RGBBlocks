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
			CompoundNBT compound = stack.getTag();
			Color color;
			color = new Color(compound.getInt("red"), compound.getInt("green"), compound.getInt("blue"));
			return color.getRGB();
		} else {
			return 0;
		}
	}
}
