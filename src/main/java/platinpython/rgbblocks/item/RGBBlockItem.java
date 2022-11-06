package platinpython.rgbblocks.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.ClientUtils;
import platinpython.rgbblocks.util.Color;

import java.util.List;

public class RGBBlockItem extends BlockItem {
    public RGBBlockItem(Block blockIn) {
        super(blockIn, new Item.Properties().tab(RGBBlocks.ITEM_GROUP_RGB));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowedIn(group)) {
            items.add(getDefaultInstance());
        }
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        CompoundTag compound = stack.getOrCreateTag();
        compound.putInt("color", -1);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Color color = new Color(stack.getOrCreateTag().getInt("color"));
        if (ClientUtils.hasShiftDown()) {
            MutableComponent red = Component.translatable("gui.rgbblocks.red").append(": " + color.getRed());
            MutableComponent green = Component.translatable("gui.rgbblocks.green").append(": " + color.getGreen());
            MutableComponent blue = Component.translatable("gui.rgbblocks.blue").append(": " + color.getBlue());
            tooltip.add(red.append(", ").append(green).append(", ").append(blue));
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
            MutableComponent hue = Component.translatable("gui.rgbblocks.hue")
                                            .append(": " + Math.round(hsb[0] * ColorSelectScreen.MAX_VALUE_HUE));
            MutableComponent saturation = Component.translatable("gui.rgbblocks.saturation")
                                                   .append(": " + Math.round(hsb[1] * ColorSelectScreen.MAX_VALUE_SB));
            MutableComponent brightness = Component.translatable("gui.rgbblocks.brightness")
                                                   .append(": " + Math.round(hsb[2] * ColorSelectScreen.MAX_VALUE_SB));
            tooltip.add(hue.append("°, ").append(saturation).append("%, ").append(brightness).append("%"));
        } else {
            tooltip.add(Component.literal("#" + Integer.toHexString(color.getRGB()).substring(2)));
        }
    }
}
