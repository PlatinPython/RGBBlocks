package platinpython.rgbblocks.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.gui.PaintbucketScreen;
import platinpython.rgbblocks.tileentity.RGBLampTileEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class PaintbucketItem extends Item {
	public PaintbucketItem() {
		super(new Properties().group(RGBBlocks.ITEM_GROUP_RGB).maxStackSize(1));
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (group == RGBBlocks.ITEM_GROUP_RGB || group == ItemGroup.SEARCH) {
			ItemStack stack = new ItemStack(this);
			CompoundNBT compound = stack.getOrCreateTag();
			compound.putInt("red", 255);
			compound.putInt("green", 255);
			compound.putInt("blue", 255);
			items.add(stack);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (playerIn.isSneaking() && handIn == Hand.MAIN_HAND) {
			Minecraft.getInstance()
					.displayGuiScreen(new PaintbucketScreen(playerIn.getHeldItemMainhand().getTag().getInt("red"),
							playerIn.getHeldItemMainhand().getTag().getInt("green"),
							playerIn.getHeldItemMainhand().getTag().getInt("blue")));
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItemMainhand());
		}
		return new ActionResult<ItemStack>(ActionResultType.PASS, playerIn.getHeldItemMainhand());
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		TileEntity tileEntity = context.getWorld().getTileEntity(context.getPos());
		if (tileEntity instanceof RGBTileEntity) {
			((RGBTileEntity) tileEntity).red = context.getItem().getTag().getInt("red");
			((RGBTileEntity) tileEntity).green = context.getItem().getTag().getInt("green");
			((RGBTileEntity) tileEntity).blue = context.getItem().getTag().getInt("blue");
			context.getWorld().notifyBlockUpdate(context.getPos(), tileEntity.getBlockState(),
					tileEntity.getBlockState(), -1);
			return ActionResultType.SUCCESS;
		} else if (tileEntity instanceof RGBLampTileEntity) {
			((RGBLampTileEntity) tileEntity).red = context.getItem().getTag().getInt("red");
			((RGBLampTileEntity) tileEntity).green = context.getItem().getTag().getInt("green");
			((RGBLampTileEntity) tileEntity).blue = context.getItem().getTag().getInt("blue");
			context.getWorld().notifyBlockUpdate(context.getPos(), tileEntity.getBlockState(),
					tileEntity.getBlockState(), -1);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.PASS;
		}
	}
}
