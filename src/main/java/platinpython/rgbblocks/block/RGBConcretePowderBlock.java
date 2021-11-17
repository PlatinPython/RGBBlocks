package platinpython.rgbblocks.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class RGBConcretePowderBlock extends ConcretePowderBlock implements EntityBlock {
    public RGBConcretePowderBlock() {
        super(BlockRegistry.RGB_CONCRETE.get(), Properties.copy(Blocks.WHITE_CONCRETE_POWDER));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RGBBlockUtils.newBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        RGBBlockUtils.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return RGBBlockUtils.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        if (worldIn.isEmptyBlock(pos.below()) || isFree(worldIn.getBlockState(pos.below())) && pos.getY() >= 0) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            RGBFallingBlockEntity fallingBlockEntity = new RGBFallingBlockEntity(worldIn,
                                                                                 (double) pos.getX() + 0.5D,
                                                                                 (double) pos.getY(),
                                                                                 (double) pos.getZ() + 0.5D,
                                                                                 state,
                                                                                 tileEntity instanceof RGBTileEntity
                                                                                 ? ((RGBTileEntity) tileEntity).getColor()
                                                                                 : 0);
            this.falling(fallingBlockEntity);
            worldIn.addFreshEntity(fallingBlockEntity);
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(BlockRegistry.RGB_CONCRETE_POWDER.get()) && !newState.is(BlockRegistry.RGB_CONCRETE.get())) {
            if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
                worldIn.removeBlockEntity(pos);
            }
        }
    }

    @Override
    public void onLand(Level world, BlockPos blockPos, BlockState blockBlockState, BlockState entityBlockState,
                       FallingBlockEntity entity) {
        super.onLand(world, blockPos, blockBlockState, entityBlockState, entity);
        if (entity instanceof RGBFallingBlockEntity) {
            RGBTileEntity tileEntity = new RGBTileEntity(blockPos, entityBlockState);
            tileEntity.setColor(((RGBFallingBlockEntity) entity).getColor());
            world.setBlockEntity(tileEntity);
        }
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockReader, BlockPos blockPos) {
        if (blockReader != null) {
            BlockEntity tileEntity = blockReader.getBlockEntity(blockPos.above());
            if (tileEntity instanceof RGBTileEntity) {
                return ((RGBTileEntity) tileEntity).getColor();
            }
        }
        return super.getDustColor(blockState, blockReader, blockPos);
    }
}
