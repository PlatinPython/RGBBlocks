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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.Color;

public class RGBBlockItem extends BlockItem {
	public RGBBlockItem(Block blockIn) {
		super(blockIn, new Item.Properties().tab(RGBBlocks.ITEM_GROUP_RGB));
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if (allowdedIn(group)) {
			ItemStack stack = new ItemStack(this);
			CompoundNBT compound = stack.getOrCreateTag();
			compound.putInt("color", -1);
			items.add(stack);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		Color color = new Color(stack.getOrCreateTag().getInt("color"));
		StringTextComponent rgbHex = new StringTextComponent("#" + Integer.toHexString(color.getRGB()).substring(2));
		tooltip.add(rgbHex);
	}
}
