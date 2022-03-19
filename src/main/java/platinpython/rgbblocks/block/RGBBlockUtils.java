package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.HitResult;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public final class RGBBlockUtils {
    public static BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TileEntityRegistry.RGB.get().create(pos, state);
    }

    public static void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer,
                                   ItemStack stack) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (stack.hasTag() && tileEntity instanceof RGBTileEntity) {
            ((RGBTileEntity) tileEntity).setColor(stack.getTag().getInt("color"));
        }
    }

    public static ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
                                         Player player) {
        ItemStack stack = new ItemStack(state.getBlock().asItem());
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof RGBTileEntity) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("color", ((RGBTileEntity) tileEntity).getColor());
            stack.setTag(tag);
        }
        return stack;
    }

    public static float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos,
                                                   BlockPos beaconPos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof RGBTileEntity) {
            return new Color(((RGBTileEntity) tileEntity).getColor()).getRGBColorComponents();
        } else {
            return null;
        }
    }

    public static boolean blockSkipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (adjacentBlockState.getBlock() instanceof RGBBlock) {
            return true;
        } else if (adjacentBlockState.getBlock() instanceof RGBGlassSlabBlock) {
            if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
                return true;
            } else if (side == Direction.UP) {
                return adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
            } else if (side == Direction.DOWN) {
                return adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP;
            } else {
                return false;
            }
        } else if (adjacentBlockState.getBlock() instanceof RGBGlassStairsBlock) {
            if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.OUTER_LEFT ||
                adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
                return false;
            } else if (side == adjacentBlockState.getValue(StairBlock.FACING).getOpposite()) {
                return true;
            } else if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_LEFT) {
                return side == adjacentBlockState.getValue(StairBlock.FACING).getClockWise();
            } else if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_RIGHT) {
                return side == adjacentBlockState.getValue(StairBlock.FACING).getCounterClockWise();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean slabSkipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (adjacentBlockState.getBlock() instanceof RGBGlassBlock) {
            return true;
        } else if (adjacentBlockState.getBlock() instanceof RGBGlassSlabBlock) {
            return slabSkipRenderingAdjacentGlassSlab(state, adjacentBlockState, side);
        } else if (adjacentBlockState.getBlock() instanceof RGBGlassStairsBlock) {
            return slabSkipRenderingAdjacentGlassStairs(state, adjacentBlockState, side);
        } else {
            return false;
        }
    }

    public static boolean slabSkipRenderingAdjacentGlassSlab(BlockState state, BlockState adjacentBlockState,
                                                             Direction side) {
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

    public static boolean slabSkipRenderingAdjacentGlassStairs(BlockState state, BlockState adjacentBlockState,
                                                               Direction side) {
        if (side == Direction.UP && adjacentBlockState.getValue(StairBlock.HALF) == Half.BOTTOM) {
            return true;
        }

        if (side == Direction.DOWN && adjacentBlockState.getValue(StairBlock.HALF) == Half.TOP) {
            return true;
        }

        if (adjacentBlockState.getValue(StairBlock.FACING) == side.getOpposite()) {
            return true;
        }

        if (side.get2DDataValue() != -1) {
            if (state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM &&
                adjacentBlockState.getValue(StairBlock.HALF) == Half.BOTTOM) {
                return true;
            } else if (state.getValue(SlabBlock.TYPE) == SlabType.TOP &&
                       adjacentBlockState.getValue(StairBlock.HALF) == Half.TOP) {
                return true;
            }
        }

        return false;
    }

    public static boolean stairSkipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (adjacentBlockState.getBlock() instanceof RGBGlassBlock) {
            return true;
        } else if (adjacentBlockState.getBlock() instanceof RGBGlassSlabBlock) {
            return stairSkipRenderingAdjacentGlassSlab(state, adjacentBlockState, side);
        } else if (adjacentBlockState.getBlock() instanceof RGBGlassStairsBlock) {
            return stairSkipRenderingAdjacentGlassStairs(state, adjacentBlockState, side);
        } else {
            return false;
        }
    }

    public static boolean stairSkipRenderingAdjacentGlassSlab(BlockState state, BlockState adjacentBlockState,
                                                              Direction side) {
        if (side == Direction.UP && adjacentBlockState.getValue(SlabBlock.TYPE) != SlabType.TOP) {
            return true;
        }

        if (side == Direction.DOWN && adjacentBlockState.getValue(SlabBlock.TYPE) != SlabType.BOTTOM) {
            return true;
        }

        if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
            return true;
        }

        if (side == state.getValue(StairBlock.FACING).getOpposite()) {
            if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP &&
                state.getValue(StairBlock.HALF) == Half.TOP) {
                return true;
            } else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM &&
                       state.getValue(StairBlock.HALF) == Half.BOTTOM) {
                return true;
            }
        }

        if (side == state.getValue(StairBlock.FACING).getClockWise() &&
            state.getValue(StairBlock.SHAPE) == StairsShape.OUTER_LEFT) {
            if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP &&
                state.getValue(StairBlock.HALF) == Half.TOP) {
                return true;
            } else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM &&
                       state.getValue(StairBlock.HALF) == Half.BOTTOM) {
                return true;
            }
        }

        if (side == state.getValue(StairBlock.FACING).getCounterClockWise() &&
            state.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
            if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP &&
                state.getValue(StairBlock.HALF) == Half.TOP) {
                return true;
            } else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM &&
                       state.getValue(StairBlock.HALF) == Half.BOTTOM) {
                return true;
            }
        }

        return false;
    }

    public static boolean stairSkipRenderingAdjacentGlassStairs(BlockState state, BlockState adjacentBlockState,
                                                                Direction side) {
        if (side == Direction.UP) {
            if (adjacentBlockState.getValue(StairBlock.HALF) == Half.BOTTOM) {
                return true;
            } else if (state.getValue(StairBlock.HALF) != adjacentBlockState.getValue(StairBlock.HALF)) {
                if (state.getValue(StairBlock.FACING) == adjacentBlockState.getValue(StairBlock.FACING) &&
                    state.getValue(StairBlock.SHAPE) == adjacentBlockState.getValue(StairBlock.SHAPE)) {
                    return true;
                } else {
                    switch (state.getValue(StairBlock.SHAPE)) {
                        case STRAIGHT:
                            if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_LEFT &&
                                (adjacentBlockState.getValue(StairBlock.FACING) == state.getValue(StairBlock.FACING) ||
                                 adjacentBlockState.getValue(StairBlock.FACING) ==
                                 state.getValue(StairBlock.FACING).getClockWise())) {
                                return true;
                            } else if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_RIGHT &&
                                       (adjacentBlockState.getValue(StairBlock.FACING) ==
                                        state.getValue(StairBlock.FACING) ||
                                        adjacentBlockState.getValue(StairBlock.FACING) ==
                                        state.getValue(StairBlock.FACING).getCounterClockWise())) {
                                return true;
                            }
                            break;
                        case INNER_LEFT:
                            if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_RIGHT &&
                                adjacentBlockState.getValue(StairBlock.FACING) ==
                                state.getValue(StairBlock.FACING).getCounterClockWise()) {
                                return true;
                            }
                            break;
                        case INNER_RIGHT:
                            if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_LEFT &&
                                adjacentBlockState.getValue(StairBlock.FACING) ==
                                state.getValue(StairBlock.FACING).getClockWise()) {
                                return true;
                            }
                            break;
                        case OUTER_LEFT:
                            if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT &&
                                adjacentBlockState.getValue(StairBlock.FACING) ==
                                state.getValue(StairBlock.FACING).getCounterClockWise()) {
                                return true;
                            } else if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT &&
                                       (adjacentBlockState.getValue(StairBlock.FACING) ==
                                        state.getValue(StairBlock.FACING) ||
                                        adjacentBlockState.getValue(StairBlock.FACING) ==
                                        state.getValue(StairBlock.FACING).getCounterClockWise())) {
                                return true;
                            }
                            break;
                        case OUTER_RIGHT:
                            if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.OUTER_LEFT &&
                                adjacentBlockState.getValue(StairBlock.FACING) ==
                                state.getValue(StairBlock.FACING).getClockWise()) {
                                return true;
                            } else if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT &&
                                       (adjacentBlockState.getValue(StairBlock.FACING) ==
                                        state.getValue(StairBlock.FACING) ||
                                        adjacentBlockState.getValue(StairBlock.FACING) ==
                                        state.getValue(StairBlock.FACING).getClockWise())) {
                                return true;
                            }
                            break;
                    }
                }
            }
        }

        if (side == Direction.DOWN) {
            if (adjacentBlockState.getValue(StairBlock.HALF) == Half.TOP) {
                return true;
            } else {
                switch (state.getValue(StairBlock.SHAPE)) {
                    case STRAIGHT:
                        if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_LEFT &&
                            (adjacentBlockState.getValue(StairBlock.FACING) == state.getValue(StairBlock.FACING) ||
                             adjacentBlockState.getValue(StairBlock.FACING) ==
                             state.getValue(StairBlock.FACING).getClockWise())) {
                            return true;
                        } else if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_RIGHT &&
                                   (adjacentBlockState.getValue(StairBlock.FACING) ==
                                    state.getValue(StairBlock.FACING) ||
                                    adjacentBlockState.getValue(StairBlock.FACING) ==
                                    state.getValue(StairBlock.FACING).getCounterClockWise())) {
                            return true;
                        }
                        break;
                    case INNER_LEFT:
                        if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_RIGHT &&
                            adjacentBlockState.getValue(StairBlock.FACING) ==
                            state.getValue(StairBlock.FACING).getCounterClockWise()) {
                            return true;
                        }
                        break;
                    case INNER_RIGHT:
                        if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_LEFT &&
                            adjacentBlockState.getValue(StairBlock.FACING) ==
                            state.getValue(StairBlock.FACING).getClockWise()) {
                            return true;
                        }
                        break;
                    case OUTER_LEFT:
                        if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT &&
                            adjacentBlockState.getValue(StairBlock.FACING) ==
                            state.getValue(StairBlock.FACING).getCounterClockWise()) {
                            return true;
                        } else if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT &&
                                   (adjacentBlockState.getValue(StairBlock.FACING) ==
                                    state.getValue(StairBlock.FACING) ||
                                    adjacentBlockState.getValue(StairBlock.FACING) ==
                                    state.getValue(StairBlock.FACING).getCounterClockWise())) {
                            return true;
                        }
                        break;
                    case OUTER_RIGHT:
                        if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.OUTER_LEFT &&
                            adjacentBlockState.getValue(StairBlock.FACING) ==
                            state.getValue(StairBlock.FACING).getClockWise()) {
                            return true;
                        } else if (adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT &&
                                   (adjacentBlockState.getValue(StairBlock.FACING) ==
                                    state.getValue(StairBlock.FACING) ||
                                    adjacentBlockState.getValue(StairBlock.FACING) ==
                                    state.getValue(StairBlock.FACING).getClockWise())) {
                            return true;
                        }
                        break;
                }
            }
        }

        if (adjacentBlockState.getValue(StairBlock.FACING) == side.getOpposite()) {
            return true;
        }

        if (side == state.getValue(StairBlock.FACING)) {
            if (state.getValue(StairBlock.HALF) == adjacentBlockState.getValue(StairBlock.HALF) &&
                state.getValue(StairBlock.SHAPE) != StairsShape.STRAIGHT) {
                if (adjacentBlockState.getValue(StairBlock.FACING) ==
                    state.getValue(StairBlock.FACING).getCounterClockWise() &&
                    adjacentBlockState.getValue(StairBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairBlock.FACING) ==
                           state.getValue(StairBlock.FACING).getClockWise() &&
                           adjacentBlockState.getValue(StairBlock.SHAPE) != StairsShape.OUTER_LEFT) {
                    return true;
                }
            }
        }

        if (side == state.getValue(StairBlock.FACING).getOpposite()) {
            if (state.getValue(StairBlock.HALF) == adjacentBlockState.getValue(StairBlock.HALF)) {
                if (adjacentBlockState.getValue(StairBlock.FACING) ==
                    state.getValue(StairBlock.FACING).getCounterClockWise() &&
                    adjacentBlockState.getValue(StairBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairBlock.FACING) ==
                           state.getValue(StairBlock.FACING).getClockWise() &&
                           adjacentBlockState.getValue(StairBlock.SHAPE) != StairsShape.OUTER_LEFT) {
                    return true;
                }
            }
        }

        if (side == state.getValue(StairBlock.FACING).getCounterClockWise()) {
            if (state.getValue(StairBlock.HALF) == adjacentBlockState.getValue(StairBlock.HALF)) {
                if (adjacentBlockState.getValue(StairBlock.FACING) == side &&
                    state.getValue(StairBlock.SHAPE) != StairsShape.INNER_LEFT &&
                    adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_RIGHT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairBlock.FACING) == state.getValue(StairBlock.FACING) &&
                           adjacentBlockState.getValue(StairBlock.SHAPE) != StairsShape.OUTER_LEFT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairBlock.FACING) ==
                           state.getValue(StairBlock.FACING).getOpposite() &&
                           state.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
                    return true;
                }
            }
        }

        if (side == state.getValue(StairBlock.FACING).getClockWise()) {
            if (state.getValue(StairBlock.HALF) == adjacentBlockState.getValue(StairBlock.HALF)) {
                if (adjacentBlockState.getValue(StairBlock.FACING) == side &&
                    state.getValue(StairBlock.SHAPE) != StairsShape.INNER_RIGHT &&
                    adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.INNER_LEFT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairBlock.FACING) == state.getValue(StairBlock.FACING) &&
                           adjacentBlockState.getValue(StairBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairBlock.FACING) ==
                           state.getValue(StairBlock.FACING).getOpposite() &&
                           state.getValue(StairBlock.SHAPE) == StairsShape.OUTER_LEFT) {
                    return true;
                }
            }
        }

        return false;
    }
}
