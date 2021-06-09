package platinpython.rgbblocks.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class DispensePaintbucketBehaviour extends DefaultDispenseItemBehavior {
	@Override
	protected ItemStack execute(IBlockSource source, ItemStack itemStack) {
		Direction dispenserFacing = source.getBlockState().getValue(DispenserBlock.FACING);
		BlockPos blockPos = source.getPos().relative(dispenserFacing);
		TileEntity tileEntity = source.getLevel().getBlockEntity(blockPos);
		if (tileEntity instanceof RGBTileEntity) {
			((RGBTileEntity) tileEntity).setColor(itemStack.getTag().getInt("color"));
			source.getLevel().sendBlockUpdated(blockPos, tileEntity.getBlockState(), tileEntity.getBlockState(), 2);
			return itemStack;
		} else {
			IPosition iposition = DispenserBlock.getDispensePosition(source);
			ItemStack spawnStack = itemStack.split(1);
			spawnItem(source.getLevel(), spawnStack, 6, dispenserFacing, iposition);
			return itemStack;
		}
	}
	
	@Override
	protected void playAnimation(IBlockSource source, Direction direction) {
	}
}
