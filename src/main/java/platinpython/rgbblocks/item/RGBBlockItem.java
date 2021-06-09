package platinpython.rgbblocks.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import platinpython.rgbblocks.RGBBlocks;

public class RGBBlockItem extends BlockItem {
	public RGBBlockItem(Block blockIn) {
		super(blockIn, new Item.Properties().tab(RGBBlocks.ITEM_GROUP_RGB));
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if (group == RGBBlocks.ITEM_GROUP_RGB || group == ItemGroup.TAB_SEARCH) {
			ItemStack stack = new ItemStack(this);
			CompoundNBT compound = stack.getOrCreateTag();
			compound.putInt("color", -1);
			items.add(stack);
		}
	}
}
