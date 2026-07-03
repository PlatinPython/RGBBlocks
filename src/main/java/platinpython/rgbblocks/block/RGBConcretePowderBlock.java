package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class RGBConcretePowderBlock extends ConcretePowderBlock implements EntityBlock {
    public RGBConcretePowderBlock() {
        super(BlockRegistry.RGB_CONCRETE.get(), Properties.ofFullCopy(Blocks.WHITE_CONCRETE_POWDER));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RGBBlockUtils.newBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(
        BlockState state,
        HitResult target,
        LevelReader level,
        BlockPos pos,
        Player player
    ) {
        return RGBBlockUtils.getCloneItemStack(state, level, pos);
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        return RGBBlockUtils.getMapColor(level, pos, defaultColor);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            RGBFallingBlockEntity fallingBlockEntity = new RGBFallingBlockEntity(
                level, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D,
                state.hasProperty(BlockStateProperties.WATERLOGGED)
                    ? state.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE)
                    : state,
                blockEntity instanceof RGBBlockEntity rgbBlockEntity ? rgbBlockEntity.getColor() : 0
            );
            level.setBlock(pos, state.getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
            level.addFreshEntity(fallingBlockEntity);
            this.falling(fallingBlockEntity);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(BlockRegistry.RGB_CONCRETE_POWDER.get()) && !newState.is(BlockRegistry.RGB_CONCRETE.get())) {
            if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
                level.removeBlockEntity(pos);
            }
        }
    }

    @Override
    public void onLand(
        Level level,
        BlockPos pos,
        BlockState state,
        BlockState replaceableState,
        FallingBlockEntity fallingBlock
    ) {
        super.onLand(level, pos, state, replaceableState, fallingBlock);
        if (fallingBlock instanceof RGBFallingBlockEntity rgbFallingBlock) {
            if (level.getBlockEntity(pos) instanceof RGBBlockEntity rgbBlockEntity) {
                rgbBlockEntity.setColor(rgbFallingBlock.getColor());
            }
        }
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockReader, BlockPos blockPos) {
        BlockEntity blockEntity = blockReader.getBlockEntity(blockPos.above());
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            return rgbBlockEntity.getColor();
        }
        return super.getDustColor(blockState, blockReader, blockPos);
    }
}
