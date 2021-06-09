package platinpython.rgbblocks.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.ClientHandler;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class PaintBucketItem extends Item {
	public PaintBucketItem() {
		super(new Properties().tab(RGBBlocks.ITEM_GROUP_RGB).defaultDurability(500).setNoRepair());
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if (group == RGBBlocks.ITEM_GROUP_RGB || group == ItemGroup.TAB_SEARCH) {
			ItemStack stack = new ItemStack(this);
			CompoundNBT compound = stack.getOrCreateTag();
			compound.putInt("color", -1);
			compound.putBoolean("isRGBSelected", true);
			items.add(stack);
		}
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return super.getMaxDamage(stack);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		super.setDamage(stack, damage);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (handIn == Hand.MAIN_HAND && playerIn.isShiftKeyDown()) {
			if (worldIn.isClientSide) {
				ClientHandler.openPaintbucketGUI(playerIn.getMainHandItem());
				return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getMainHandItem());
			}
		}
		return new ActionResult<ItemStack>(ActionResultType.PASS, playerIn.getItemInHand(handIn));
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		TileEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
		if (tileEntity instanceof RGBTileEntity) {
			if (context.getPlayer().isShiftKeyDown()) {
				context.getItemInHand().getTag().putInt("color", ((RGBTileEntity) tileEntity).getColor());
			} else {
				if (context.getItemInHand().getOrCreateTag().getInt("color") != ((RGBTileEntity) tileEntity)
						.getColor()) {
					if (context.getItemInHand().getDamageValue() == context.getItemInHand().getMaxDamage() - 1) {
						context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
					} else {
						context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
								e -> e.broadcastBreakEvent(context.getHand()));
					}
				}
				((RGBTileEntity) tileEntity).setColor(context.getItemInHand().getTag().getInt("color"));
				context.getLevel().sendBlockUpdated(context.getClickedPos(), tileEntity.getBlockState(),
						tileEntity.getBlockState(), 2);
			}
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.PASS;
		}
	}
}
