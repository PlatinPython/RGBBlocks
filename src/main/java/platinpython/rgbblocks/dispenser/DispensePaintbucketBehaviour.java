package platinpython.rgbblocks.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.BlockFlags;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class DispensePaintbucketBehaviour extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack execute(IBlockSource source, ItemStack itemStack) {
        Direction dispenserFacing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos blockPos = source.getPos().relative(dispenserFacing);
        TileEntity tileEntity = source.getLevel().getBlockEntity(blockPos);
        if (tileEntity instanceof RGBTileEntity) {
            boolean broke = false;
            if (itemStack.getOrCreateTag().getInt("color") != ((RGBTileEntity) tileEntity).getColor()) {
                if (itemStack.getDamageValue() == itemStack.getMaxDamage() - 1) {
                    broke = true;
                } else {
                    itemStack.hurt(1, source.getLevel().random, null);
                }
            }
            ((RGBTileEntity) tileEntity).setColor(itemStack.getOrCreateTag().getInt("color"));
            source.getLevel()
                  .sendBlockUpdated(blockPos,
                                    tileEntity.getBlockState(),
                                    tileEntity.getBlockState(),
                                    BlockFlags.DEFAULT_AND_RERENDER);
            return broke ? new ItemStack(Items.BUCKET) : itemStack;
        } else {
            return super.execute(source, itemStack);
        }
    }
}
