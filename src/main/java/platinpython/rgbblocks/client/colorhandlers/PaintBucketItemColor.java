package platinpython.rgbblocks.client.colorhandlers;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import platinpython.rgbblocks.item.PaintBucketItem;

public class PaintBucketItemColor implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (stack.getItem() instanceof PaintBucketItem) {
            if (tintIndex == 1) {
                // noinspection DataFlowIssue
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
