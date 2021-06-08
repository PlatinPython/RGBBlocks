package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;

public interface IGlassSlabBlock extends IGlassBlock {
	static boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.getBlock() instanceof GenericRGBGlassBlock) {
			return true;
		} else if (adjacentBlockState.getBlock() instanceof GenericRGBGlassSlabBlock) {
			return isInvisibleToGlassSlab(state, adjacentBlockState, side);
		} else if (adjacentBlockState.getBlock() instanceof GenericRGBGlassStairsBlock) {
			return isInvisibleToGlassStairs(state, adjacentBlockState, side);
		} else {
			return false;
		}
	}

	static boolean isInvisibleToGlassSlab(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
			return true;
		}

		switch (side) {
		case UP:
		case DOWN:
			return (state.getValue(SlabBlock.TYPE) != adjacentBlockState.getValue(SlabBlock.TYPE));
		case NORTH:
		case EAST:
		case SOUTH:
		case WEST:
			return (state.getValue(SlabBlock.TYPE) == adjacentBlockState.getValue(SlabBlock.TYPE));
		}

		return false;
	}

	static boolean isInvisibleToGlassStairs(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (side == Direction.UP && adjacentBlockState.getValue(StairsBlock.HALF) == Half.BOTTOM) {
			return true;
		}

		if (side == Direction.DOWN && adjacentBlockState.getValue(StairsBlock.HALF) == Half.TOP) {
			return true;
		}

		if (adjacentBlockState.getValue(StairsBlock.FACING) == side.getOpposite()) {
			return true;
		}

		if (side.get2DDataValue() != -1) {
			if (state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM
					&& adjacentBlockState.getValue(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			} else if (state.getValue(SlabBlock.TYPE) == SlabType.TOP
					&& adjacentBlockState.getValue(StairsBlock.HALF) == Half.TOP) {
				return true;
			}
		}

		return false;
	}
}
