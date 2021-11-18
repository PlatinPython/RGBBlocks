package platinpython.rgbblocks.item;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

public class RGBBlockItem extends BlockItem {
    public RGBBlockItem(Block blockIn) {
        super(blockIn, new Item.Properties().tab(RGBBlocks.ITEM_GROUP_RGB));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
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
            MutableComponent red = new TranslatableComponent("gui.rgbblocks.red").append(": " + color.getRed());
            MutableComponent green = new TranslatableComponent("gui.rgbblocks.green").append(": " + color.getGreen());
            MutableComponent blue = new TranslatableComponent("gui.rgbblocks.blue").append(": " + color.getBlue());
            tooltip.add(red.append(", ").append(green).append(", ").append(blue));
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
            MutableComponent hue = new TranslatableComponent("gui.rgbblocks.hue").append(": " +
                                                                                         Math.round(hsb[0] *
                                                                                                    ColorSelectScreen.MAX_VALUE_HUE));
            MutableComponent saturation = new TranslatableComponent("gui.rgbblocks.saturation").append(": " +
                                                                                                       Math.round(hsb[1] *
                                                                                                                  ColorSelectScreen.MAX_VALUE_SB));
            MutableComponent brightness = new TranslatableComponent("gui.rgbblocks.brightness").append(": " +
                                                                                                       Math.round(hsb[2] *
                                                                                                                  ColorSelectScreen.MAX_VALUE_SB));
            tooltip.add(hue.append("°, ").append(saturation).append("%, ").append(brightness).append("%"));
        } else {
            tooltip.add(new TextComponent("#" + Integer.toHexString(color.getRGB()).substring(2)));
        }
    }
}
