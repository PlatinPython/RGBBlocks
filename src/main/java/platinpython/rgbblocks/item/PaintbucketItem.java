package platinpython.rgbblocks.item;

import java.awt.Color;

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
import platinpython.rgbblocks.client.ClientHandler;
import platinpython.rgbblocks.tileentity.RGBLampTileEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class PaintbucketItem extends Item {
	public PaintbucketItem() {
		super(new Properties().tab(RGBBlocks.ITEM_GROUP_RGB).stacksTo(1));
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if (group == RGBBlocks.ITEM_GROUP_RGB || group == ItemGroup.TAB_SEARCH) {
			ItemStack stack = new ItemStack(this);
			CompoundNBT compound = stack.getOrCreateTag();
			compound.putInt("color", Color.WHITE.getRGB());
			compound.putBoolean("isRGBSelected", true);
			items.add(stack);
		}
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (playerIn.isShiftKeyDown() && handIn == Hand.MAIN_HAND) {
			if (worldIn.isClientSide) {
				ClientHandler.openPaintbucketGUI(playerIn.getMainHandItem());
				return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getMainHandItem());
			}
		}
		return new ActionResult<ItemStack>(ActionResultType.PASS, playerIn.getMainHandItem());
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		TileEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
		if (tileEntity instanceof RGBTileEntity || tileEntity instanceof RGBLampTileEntity) {
			((RGBTileEntity) tileEntity).setColor(context.getItemInHand().getTag().getInt("color"));
			context.getLevel().sendBlockUpdated(context.getClickedPos(), tileEntity.getBlockState(),
					tileEntity.getBlockState(), 2);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.PASS;
		}
	}
}
