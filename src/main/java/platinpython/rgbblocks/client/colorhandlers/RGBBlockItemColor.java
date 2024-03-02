package platinpython.rgbblocks.client.colorhandlers;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import platinpython.rgbblocks.item.RGBBlockItem;

public class RGBBlockItemColor implements ItemColor {
    public int getColor(ItemStack stack, int tintIndex) {
        if (stack.getItem() instanceof RGBBlockItem) {
            // noinspection DataFlowIssue
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
