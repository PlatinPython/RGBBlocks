package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGlassStairsBlock {
	@OnlyIn(Dist.CLIENT)
	public static boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.getBlock() instanceof RGBGlassFlatBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatBlock) {
			return true;
		} else if (adjacentBlockState.getBlock() instanceof RGBGlassFlatSlabBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatSlabBlock) {
			return isInvisibleToGlassSlab(state, adjacentBlockState, side);
		} else if (adjacentBlockState.getBlock() instanceof RGBGlassFlatStairsBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatStairsBlock) {
			return isInvisibleToGlassStairs(state, adjacentBlockState, side);
		} else {
			return false;
		}
	}

	static boolean isInvisibleToGlassSlab(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (side == Direction.UP && adjacentBlockState.get(SlabBlock.TYPE) != SlabType.TOP) {
			return true;
		}

		if (side == Direction.DOWN && adjacentBlockState.get(SlabBlock.TYPE) != SlabType.BOTTOM) {
			return true;
		}

		if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
			return true;
		}

		if (side == state.get(StairsBlock.FACING).getOpposite()) {
			if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.TOP && state.get(StairsBlock.HALF) == Half.TOP) {
				return true;
			} else if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.BOTTOM
					&& state.get(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			}
		}

		if (side == state.get(StairsBlock.FACING).rotateY() && state.get(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT) {
			if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.TOP && state.get(StairsBlock.HALF) == Half.TOP) {
				return true;
			} else if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.BOTTOM
					&& state.get(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			}
		}

		if (side == state.get(StairsBlock.FACING).rotateYCCW()
				&& state.get(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
			if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.TOP && state.get(StairsBlock.HALF) == Half.TOP) {
				return true;
			} else if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.BOTTOM
					&& state.get(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			}
		}

		return false;
	}

	static boolean isInvisibleToGlassStairs(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (side == Direction.UP) {
			if (adjacentBlockState.get(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			} else if (state.get(StairsBlock.HALF) != adjacentBlockState.get(StairsBlock.HALF)) {
				if (state.get(StairsBlock.FACING) == adjacentBlockState.get(StairsBlock.FACING)
						&& state.get(StairsBlock.SHAPE) == adjacentBlockState.get(StairsBlock.SHAPE)) {
					return true;
				} else {
					switch (state.get(StairsBlock.SHAPE)) {
					case STRAIGHT:
						if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_LEFT
								&& (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
										|| adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
												.rotateY())) {
							return true;
						} else if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT
								&& (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
										|| adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
												.rotateYCCW())) {
							return true;
						}
						break;
					case INNER_LEFT:
						if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT && adjacentBlockState
								.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateYCCW()) {
							return true;
						}
						break;
					case INNER_RIGHT:
						if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_LEFT && adjacentBlockState
								.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateY()) {
							return true;
						}
						break;
					case OUTER_LEFT:
						if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT && adjacentBlockState
								.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateYCCW()) {
							return true;
						} else if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.STRAIGHT
								&& (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
										|| adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
												.rotateYCCW())) {
							return true;
						}
						break;
					case OUTER_RIGHT:
						if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT && adjacentBlockState
								.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateY()) {
							return true;
						} else if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.STRAIGHT
								&& (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
										|| adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
												.rotateY())) {
							return true;
						}
						break;
					}
				}
			}
		}

		if (side == Direction.DOWN) {
			if (adjacentBlockState.get(StairsBlock.HALF) == Half.TOP) {
				return true;
			} else {
				switch (state.get(StairsBlock.SHAPE)) {
				case STRAIGHT:
					if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_LEFT && (adjacentBlockState
							.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
							|| adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateY())) {
						return true;
					} else if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT
							&& (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
									|| adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
											.rotateYCCW())) {
						return true;
					}
					break;
				case INNER_LEFT:
					if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT && adjacentBlockState
							.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateYCCW()) {
						return true;
					}
					break;
				case INNER_RIGHT:
					if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_LEFT
							&& adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateY()) {
						return true;
					}
					break;
				case OUTER_LEFT:
					if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT && adjacentBlockState
							.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateYCCW()) {
						return true;
					} else if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.STRAIGHT
							&& (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
									|| adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
											.rotateYCCW())) {
						return true;
					}
					break;
				case OUTER_RIGHT:
					if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT
							&& adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateY()) {
						return true;
					} else if (adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.STRAIGHT && (adjacentBlockState
							.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
							|| adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateY())) {
						return true;
					}
					break;
				}
			}
		}

		if (adjacentBlockState.get(StairsBlock.FACING) == side.getOpposite()) {
			return true;
		}

		if (side == state.get(StairsBlock.FACING)) {
			if (state.get(StairsBlock.HALF) == adjacentBlockState.get(StairsBlock.HALF)
					&& state.get(StairsBlock.SHAPE) != StairsShape.STRAIGHT) {
				if (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateYCCW()
						&& adjacentBlockState.get(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
					return true;
				} else if (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateY()
						&& adjacentBlockState.get(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
					return true;
				}
			}
		}

		if (side == state.get(StairsBlock.FACING).getOpposite()) {
			if (state.get(StairsBlock.HALF) == adjacentBlockState.get(StairsBlock.HALF)) {
				if (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateYCCW()
						&& adjacentBlockState.get(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
					return true;
				} else if (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).rotateY()
						&& adjacentBlockState.get(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
					return true;
				}
			}
		}

		if (side == state.get(StairsBlock.FACING).rotateYCCW()) {
			if (state.get(StairsBlock.HALF) == adjacentBlockState.get(StairsBlock.HALF)) {
				if (adjacentBlockState.get(StairsBlock.FACING) == side
						&& state.get(StairsBlock.SHAPE) != StairsShape.INNER_LEFT
						&& adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT) {
					return true;
				} else if (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
						&& adjacentBlockState.get(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
					return true;
				} else if (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).getOpposite()
						&& state.get(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
					return true;
				}
			}
		}

		if (side == state.get(StairsBlock.FACING).rotateY()) {
			if (state.get(StairsBlock.HALF) == adjacentBlockState.get(StairsBlock.HALF)) {
				if (adjacentBlockState.get(StairsBlock.FACING) == side
						&& state.get(StairsBlock.SHAPE) != StairsShape.INNER_RIGHT
						&& adjacentBlockState.get(StairsBlock.SHAPE) == StairsShape.INNER_LEFT) {
					return true;
				} else if (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING)
						&& adjacentBlockState.get(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
					return true;
				} else if (adjacentBlockState.get(StairsBlock.FACING) == state.get(StairsBlock.FACING).getOpposite()
						&& state.get(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT) {
					return true;
				}
			}
		}

		return false;
	}
}
