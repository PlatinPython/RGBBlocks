package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class GenericRGBGlassBlock extends GenericRGBBlock implements IGlassBlock {
	public GenericRGBGlassBlock() {
		super(Properties.copy(Blocks.GLASS));
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
		return IGlassBlock.getBeaconColorMultiplier(state, world, pos, beaconPos);
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.getBlock() instanceof GenericRGBBlock) {
			return true;
		} else if (adjacentBlockState.getBlock() instanceof GenericRGBGlassSlabBlock) {
			if(adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
				return true;
			} else if (side == Direction.UP) {
				return adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
			} else if (side == Direction.DOWN) {
				return adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP;
			} else {
				return false;
			}
		} else if (adjacentBlockState.getBlock() instanceof GenericRGBGlassStairsBlock) {
			if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT
					|| adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
				return false;
			} else if (side == adjacentBlockState.getValue(StairsBlock.FACING).getOpposite()) {
				return true;
			} else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT) {
				return side == adjacentBlockState.getValue(StairsBlock.FACING).getClockWise();
			} else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT) {
				return side == adjacentBlockState.getValue(StairsBlock.FACING).getCounterClockWise();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
}
