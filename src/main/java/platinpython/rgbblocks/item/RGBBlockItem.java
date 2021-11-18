package platinpython.rgbblocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.ClientUtils;
import platinpython.rgbblocks.util.Color;

public class RGBBlockItem extends BlockItem {
    public RGBBlockItem(Block blockIn) {
        super(blockIn, new Item.Properties().tab(RGBBlocks.ITEM_GROUP_RGB));
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            items.add(getDefaultInstance());
        }
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putInt("color", -1);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Color color = new Color(stack.getOrCreateTag().getInt("color"));
        if (ClientUtils.hasShiftDown()) {
            IFormattableTextComponent red = new TranslationTextComponent("gui.rgbblocks.red").append(": " +
                                                                                                     color.getRed());
            IFormattableTextComponent green = new TranslationTextComponent("gui.rgbblocks.green").append(": " +
                                                                                                         color.getGreen());
            IFormattableTextComponent blue = new TranslationTextComponent("gui.rgbblocks.blue").append(": " +
                                                                                                       color.getBlue());
            tooltip.add(red.append(", ").append(green).append(", ").append(blue));
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
            IFormattableTextComponent hue = new TranslationTextComponent("gui.rgbblocks.hue").append(": " +
                                                                                                     Math.round(hsb[0] *
                                                                                                                ColorSelectScreen.MAX_VALUE_HUE));
            IFormattableTextComponent saturation = new TranslationTextComponent("gui.rgbblocks.saturation").append(": " +
                                                                                                                   Math.round(
                                                                                                                           hsb[1] *
                                                                                                                           ColorSelectScreen.MAX_VALUE_SB));
            IFormattableTextComponent brightness = new TranslationTextComponent("gui.rgbblocks.brightness").append(": " +
                                                                                                                   Math.round(
                                                                                                                           hsb[2] *
                                                                                                                           ColorSelectScreen.MAX_VALUE_SB));
            tooltip.add(hue.append("°, ").append(saturation).append("%, ").append(brightness).append("%"));
        } else {
            tooltip.add(new StringTextComponent("#" + Integer.toHexString(color.getRGB()).substring(2)));
        }
    }
}
