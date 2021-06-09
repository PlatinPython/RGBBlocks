package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;

public interface IGlassStairsBlock {
	public static boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.getBlock() instanceof GenericRGBGlassBlock) {
			return true;
		} else if (adjacentBlockState.getBlock() instanceof GenericRGBGlassSlabBlock) {
			return skipRenderingGlassSlab(state, adjacentBlockState, side);
		} else if (adjacentBlockState.getBlock() instanceof GenericRGBGlassStairsBlock) {
			return skipRenderingGlassStairs(state, adjacentBlockState, side);
		} else {
			return false;
		}
	}

	static boolean skipRenderingGlassSlab(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (side == Direction.UP && adjacentBlockState.getValue(SlabBlock.TYPE) != SlabType.TOP) {
			return true;
		}

		if (side == Direction.DOWN && adjacentBlockState.getValue(SlabBlock.TYPE) != SlabType.BOTTOM) {
			return true;
		}

		if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
			return true;
		}

		if (side == state.getValue(StairsBlock.FACING).getOpposite()) {
			if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP
					&& state.getValue(StairsBlock.HALF) == Half.TOP) {
				return true;
			} else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM
					&& state.getValue(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			}
		}

		if (side == state.getValue(StairsBlock.FACING).getClockWise()
				&& state.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT) {
			if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP
					&& state.getValue(StairsBlock.HALF) == Half.TOP) {
				return true;
			} else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM
					&& state.getValue(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			}
		}

		if (side == state.getValue(StairsBlock.FACING).getCounterClockWise()
				&& state.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
			if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP
					&& state.getValue(StairsBlock.HALF) == Half.TOP) {
				return true;
			} else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM
					&& state.getValue(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			}
		}

		return false;
	}

	static boolean skipRenderingGlassStairs(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (side == Direction.UP) {
			if (adjacentBlockState.getValue(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			} else if (state.getValue(StairsBlock.HALF) != adjacentBlockState.getValue(StairsBlock.HALF)) {
				if (state.getValue(StairsBlock.FACING) == adjacentBlockState.getValue(StairsBlock.FACING)
						&& state.getValue(StairsBlock.SHAPE) == adjacentBlockState.getValue(StairsBlock.SHAPE)) {
					return true;
				} else {
					switch (state.getValue(StairsBlock.SHAPE)) {
					case STRAIGHT:
						if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT
								&& (adjacentBlockState.getValue(StairsBlock.FACING) == state
										.getValue(StairsBlock.FACING)
										|| adjacentBlockState.getValue(StairsBlock.FACING) == state
												.getValue(StairsBlock.FACING).getClockWise())) {
							return true;
						} else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT
								&& (adjacentBlockState.getValue(StairsBlock.FACING) == state
										.getValue(StairsBlock.FACING)
										|| adjacentBlockState.getValue(StairsBlock.FACING) == state
												.getValue(StairsBlock.FACING).getCounterClockWise())) {
							return true;
						}
						break;
					case INNER_LEFT:
						if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT
								&& adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
										.getCounterClockWise()) {
							return true;
						}
						break;
					case INNER_RIGHT:
						if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT
								&& adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
										.getClockWise()) {
							return true;
						}
						break;
					case OUTER_LEFT:
						if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT
								&& adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
										.getCounterClockWise()) {
							return true;
						} else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT
								&& (adjacentBlockState.getValue(StairsBlock.FACING) == state
										.getValue(StairsBlock.FACING)
										|| adjacentBlockState.getValue(StairsBlock.FACING) == state
												.getValue(StairsBlock.FACING).getCounterClockWise())) {
							return true;
						}
						break;
					case OUTER_RIGHT:
						if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT
								&& adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
										.getClockWise()) {
							return true;
						} else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT
								&& (adjacentBlockState.getValue(StairsBlock.FACING) == state
										.getValue(StairsBlock.FACING)
										|| adjacentBlockState.getValue(StairsBlock.FACING) == state
												.getValue(StairsBlock.FACING).getClockWise())) {
							return true;
						}
						break;
					}
				}
			}
		}

		if (side == Direction.DOWN) {
			if (adjacentBlockState.getValue(StairsBlock.HALF) == Half.TOP) {
				return true;
			} else {
				switch (state.getValue(StairsBlock.SHAPE)) {
				case STRAIGHT:
					if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT
							&& (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
									|| adjacentBlockState.getValue(StairsBlock.FACING) == state
											.getValue(StairsBlock.FACING).getClockWise())) {
						return true;
					} else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT
							&& (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
									|| adjacentBlockState.getValue(StairsBlock.FACING) == state
											.getValue(StairsBlock.FACING).getCounterClockWise())) {
						return true;
					}
					break;
				case INNER_LEFT:
					if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT && adjacentBlockState
							.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING).getCounterClockWise()) {
						return true;
					}
					break;
				case INNER_RIGHT:
					if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT && adjacentBlockState
							.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING).getClockWise()) {
						return true;
					}
					break;
				case OUTER_LEFT:
					if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT && adjacentBlockState
							.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING).getCounterClockWise()) {
						return true;
					} else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT
							&& (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
									|| adjacentBlockState.getValue(StairsBlock.FACING) == state
											.getValue(StairsBlock.FACING).getCounterClockWise())) {
						return true;
					}
					break;
				case OUTER_RIGHT:
					if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT && adjacentBlockState
							.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING).getClockWise()) {
						return true;
					} else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT
							&& (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
									|| adjacentBlockState.getValue(StairsBlock.FACING) == state
											.getValue(StairsBlock.FACING).getClockWise())) {
						return true;
					}
					break;
				}
			}
		}

		if (adjacentBlockState.getValue(StairsBlock.FACING) == side.getOpposite()) {
			return true;
		}

		if (side == state.getValue(StairsBlock.FACING)) {
			if (state.getValue(StairsBlock.HALF) == adjacentBlockState.getValue(StairsBlock.HALF)
					&& state.getValue(StairsBlock.SHAPE) != StairsShape.STRAIGHT) {
				if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
						.getCounterClockWise()
						&& adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
					return true;
				} else if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
						.getClockWise() && adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
					return true;
				}
			}
		}

		if (side == state.getValue(StairsBlock.FACING).getOpposite()) {
			if (state.getValue(StairsBlock.HALF) == adjacentBlockState.getValue(StairsBlock.HALF)) {
				if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
						.getCounterClockWise()
						&& adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
					return true;
				} else if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
						.getClockWise() && adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
					return true;
				}
			}
		}

		if (side == state.getValue(StairsBlock.FACING).getCounterClockWise()) {
			if (state.getValue(StairsBlock.HALF) == adjacentBlockState.getValue(StairsBlock.HALF)) {
				if (adjacentBlockState.getValue(StairsBlock.FACING) == side
						&& state.getValue(StairsBlock.SHAPE) != StairsShape.INNER_LEFT
						&& adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT) {
					return true;
				} else if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
						&& adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
					return true;
				} else if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
						.getOpposite() && state.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
					return true;
				}
			}
		}

		if (side == state.getValue(StairsBlock.FACING).getClockWise()) {
			if (state.getValue(StairsBlock.HALF) == adjacentBlockState.getValue(StairsBlock.HALF)) {
				if (adjacentBlockState.getValue(StairsBlock.FACING) == side
						&& state.getValue(StairsBlock.SHAPE) != StairsShape.INNER_RIGHT
						&& adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT) {
					return true;
				} else if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
						&& adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
					return true;
				} else if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING)
						.getOpposite() && state.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT) {
					return true;
				}
			}
		}

		return false;
	}
}
