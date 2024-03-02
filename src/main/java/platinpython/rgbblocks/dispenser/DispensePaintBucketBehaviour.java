package platinpython.rgbblocks.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;

public class DispensePaintBucketBehaviour extends DefaultDispenseItemBehavior {
    @SuppressWarnings("resource")
    @Override
    protected ItemStack execute(BlockSource source, ItemStack itemStack) {
        Direction dispenserFacing = source.state().getValue(DispenserBlock.FACING);
        BlockPos blockPos = source.pos().relative(dispenserFacing);
        BlockEntity blockEntity = source.level().getBlockEntity(blockPos);
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            boolean broke = false;
            if (itemStack.getOrCreateTag().getInt("color") != rgbBlockEntity.getColor()) {
                if (itemStack.getDamageValue() == itemStack.getMaxDamage() - 1) {
                    broke = true;
                } else {
                    itemStack.hurt(1, source.level().random, null);
                }
            }
            rgbBlockEntity.setColor(itemStack.getOrCreateTag().getInt("color"));
            source.level()
                .sendBlockUpdated(
                    blockPos, blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_ALL_IMMEDIATE
                );
            return broke ? new ItemStack(Items.BUCKET) : itemStack;
        } else {
            return super.execute(source, itemStack);
        }
    }
}
