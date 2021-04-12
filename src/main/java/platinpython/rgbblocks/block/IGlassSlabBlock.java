package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGlassSlabBlock extends IGlassBlock {
	@OnlyIn(Dist.CLIENT)
	static boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.getBlock() instanceof GenericRGBGlassBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatBlock) {
			return true;
		} else if (adjacentBlockState.getBlock() instanceof GenericRGBGlassSlabBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatSlabBlock) {
			return isInvisibleToGlassSlab(state, adjacentBlockState, side);
		} else if (adjacentBlockState.getBlock() instanceof GenericRGBGlassStairsBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatStairsBlock) {
			return isInvisibleToGlassStairs(state, adjacentBlockState, side);
		} else {
			return false;
		}
	}

	static boolean isInvisibleToGlassSlab(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
			return true;
		}

		switch (side) {
		case UP:
		case DOWN:
			return (state.get(SlabBlock.TYPE) != adjacentBlockState.get(SlabBlock.TYPE));
		case NORTH:
		case EAST:
		case SOUTH:
		case WEST:
			return (state.get(SlabBlock.TYPE) == adjacentBlockState.get(SlabBlock.TYPE));
		}

		return false;
	}

	static boolean isInvisibleToGlassStairs(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (side == Direction.UP && adjacentBlockState.get(StairsBlock.HALF) == Half.BOTTOM) {
			return true;
		}

		if (side == Direction.DOWN && adjacentBlockState.get(StairsBlock.HALF) == Half.TOP) {
			return true;
		}

		if (adjacentBlockState.get(StairsBlock.FACING) == side.getOpposite()) {
			return true;
		}

		if (side.getHorizontalIndex() != -1) {
			if (state.get(SlabBlock.TYPE) == SlabType.BOTTOM
					&& adjacentBlockState.get(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			} else if (state.get(SlabBlock.TYPE) == SlabType.TOP
					&& adjacentBlockState.get(StairsBlock.HALF) == Half.TOP) {
				return true;
			}
		}

		return false;
	}
}
