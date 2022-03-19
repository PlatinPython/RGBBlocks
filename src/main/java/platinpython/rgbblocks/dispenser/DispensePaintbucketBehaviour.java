package platinpython.rgbblocks.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class DispensePaintbucketBehaviour extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource source, ItemStack itemStack) {
        Direction dispenserFacing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos blockPos = source.getPos().relative(dispenserFacing);
        BlockEntity tileEntity = source.getLevel().getBlockEntity(blockPos);
        if (tileEntity instanceof RGBTileEntity) {
            ((RGBTileEntity) tileEntity).setColor(itemStack.getTag().getInt("color"));
            source.getLevel()
                  .sendBlockUpdated(blockPos,
                                    tileEntity.getBlockState(),
                                    tileEntity.getBlockState(),
                                    Block.UPDATE_ALL_IMMEDIATE);
            return itemStack;
        } else {
            return super.execute(source, itemStack);
        }
    }
}
