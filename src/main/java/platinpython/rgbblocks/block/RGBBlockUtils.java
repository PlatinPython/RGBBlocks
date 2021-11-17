package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public final class RGBBlockUtils {
    public static TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityRegistry.RGB.get().create();
    }

    public static void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer,
                                   ItemStack stack) {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (stack.hasTag() == true && tileEntity instanceof RGBTileEntity) {
            ((RGBTileEntity) tileEntity).setColor(stack.getTag().getInt("color"));
        }
    }

    public static ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
                                         PlayerEntity player) {
        ItemStack stack = new ItemStack(state.getBlock().asItem());
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof RGBTileEntity) {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt("color", ((RGBTileEntity) tileEntity).getColor());
            stack.setTag(tag);
        }
        return stack;
    }

    public static float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos,
                                                   BlockPos beaconPos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
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
            if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT ||
                adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
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
            if (state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM &&
                adjacentBlockState.getValue(StairsBlock.HALF) == Half.BOTTOM) {
                return true;
            } else if (state.getValue(SlabBlock.TYPE) == SlabType.TOP &&
                       adjacentBlockState.getValue(StairsBlock.HALF) == Half.TOP) {
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

        if (side == state.getValue(StairsBlock.FACING).getOpposite()) {
            if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP &&
                state.getValue(StairsBlock.HALF) == Half.TOP) {
                return true;
            } else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM &&
                       state.getValue(StairsBlock.HALF) == Half.BOTTOM) {
                return true;
            }
        }

        if (side == state.getValue(StairsBlock.FACING).getClockWise() &&
            state.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT) {
            if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP &&
                state.getValue(StairsBlock.HALF) == Half.TOP) {
                return true;
            } else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM &&
                       state.getValue(StairsBlock.HALF) == Half.BOTTOM) {
                return true;
            }
        }

        if (side == state.getValue(StairsBlock.FACING).getCounterClockWise() &&
            state.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
            if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP &&
                state.getValue(StairsBlock.HALF) == Half.TOP) {
                return true;
            } else if (adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM &&
                       state.getValue(StairsBlock.HALF) == Half.BOTTOM) {
                return true;
            }
        }

        return false;
    }

    public static boolean stairSkipRenderingAdjacentGlassStairs(BlockState state, BlockState adjacentBlockState,
                                                                Direction side) {
        if (side == Direction.UP) {
            if (adjacentBlockState.getValue(StairsBlock.HALF) == Half.BOTTOM) {
                return true;
            } else if (state.getValue(StairsBlock.HALF) != adjacentBlockState.getValue(StairsBlock.HALF)) {
                if (state.getValue(StairsBlock.FACING) == adjacentBlockState.getValue(StairsBlock.FACING) &&
                    state.getValue(StairsBlock.SHAPE) == adjacentBlockState.getValue(StairsBlock.SHAPE)) {
                    return true;
                } else {
                    switch (state.getValue(StairsBlock.SHAPE)) {
                        case STRAIGHT:
                            if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT &&
                                (adjacentBlockState.getValue(StairsBlock.FACING) ==
                                 state.getValue(StairsBlock.FACING) ||
                                 adjacentBlockState.getValue(StairsBlock.FACING) ==
                                 state.getValue(StairsBlock.FACING).getClockWise())) {
                                return true;
                            } else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT &&
                                       (adjacentBlockState.getValue(StairsBlock.FACING) ==
                                        state.getValue(StairsBlock.FACING) ||
                                        adjacentBlockState.getValue(StairsBlock.FACING) ==
                                        state.getValue(StairsBlock.FACING).getCounterClockWise())) {
                                return true;
                            }
                            break;
                        case INNER_LEFT:
                            if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT &&
                                adjacentBlockState.getValue(StairsBlock.FACING) ==
                                state.getValue(StairsBlock.FACING).getCounterClockWise()) {
                                return true;
                            }
                            break;
                        case INNER_RIGHT:
                            if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT &&
                                adjacentBlockState.getValue(StairsBlock.FACING) ==
                                state.getValue(StairsBlock.FACING).getClockWise()) {
                                return true;
                            }
                            break;
                        case OUTER_LEFT:
                            if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT &&
                                adjacentBlockState.getValue(StairsBlock.FACING) ==
                                state.getValue(StairsBlock.FACING).getCounterClockWise()) {
                                return true;
                            } else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT &&
                                       (adjacentBlockState.getValue(StairsBlock.FACING) ==
                                        state.getValue(StairsBlock.FACING) ||
                                        adjacentBlockState.getValue(StairsBlock.FACING) ==
                                        state.getValue(StairsBlock.FACING).getCounterClockWise())) {
                                return true;
                            }
                            break;
                        case OUTER_RIGHT:
                            if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT &&
                                adjacentBlockState.getValue(StairsBlock.FACING) ==
                                state.getValue(StairsBlock.FACING).getClockWise()) {
                                return true;
                            } else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT &&
                                       (adjacentBlockState.getValue(StairsBlock.FACING) ==
                                        state.getValue(StairsBlock.FACING) ||
                                        adjacentBlockState.getValue(StairsBlock.FACING) ==
                                        state.getValue(StairsBlock.FACING).getClockWise())) {
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
                        if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT &&
                            (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING) ||
                             adjacentBlockState.getValue(StairsBlock.FACING) ==
                             state.getValue(StairsBlock.FACING).getClockWise())) {
                            return true;
                        } else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT &&
                                   (adjacentBlockState.getValue(StairsBlock.FACING) ==
                                    state.getValue(StairsBlock.FACING) ||
                                    adjacentBlockState.getValue(StairsBlock.FACING) ==
                                    state.getValue(StairsBlock.FACING).getCounterClockWise())) {
                            return true;
                        }
                        break;
                    case INNER_LEFT:
                        if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT &&
                            adjacentBlockState.getValue(StairsBlock.FACING) ==
                            state.getValue(StairsBlock.FACING).getCounterClockWise()) {
                            return true;
                        }
                        break;
                    case INNER_RIGHT:
                        if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT &&
                            adjacentBlockState.getValue(StairsBlock.FACING) ==
                            state.getValue(StairsBlock.FACING).getClockWise()) {
                            return true;
                        }
                        break;
                    case OUTER_LEFT:
                        if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT &&
                            adjacentBlockState.getValue(StairsBlock.FACING) ==
                            state.getValue(StairsBlock.FACING).getCounterClockWise()) {
                            return true;
                        } else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT &&
                                   (adjacentBlockState.getValue(StairsBlock.FACING) ==
                                    state.getValue(StairsBlock.FACING) ||
                                    adjacentBlockState.getValue(StairsBlock.FACING) ==
                                    state.getValue(StairsBlock.FACING).getCounterClockWise())) {
                            return true;
                        }
                        break;
                    case OUTER_RIGHT:
                        if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT &&
                            adjacentBlockState.getValue(StairsBlock.FACING) ==
                            state.getValue(StairsBlock.FACING).getClockWise()) {
                            return true;
                        } else if (adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT &&
                                   (adjacentBlockState.getValue(StairsBlock.FACING) ==
                                    state.getValue(StairsBlock.FACING) ||
                                    adjacentBlockState.getValue(StairsBlock.FACING) ==
                                    state.getValue(StairsBlock.FACING).getClockWise())) {
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
            if (state.getValue(StairsBlock.HALF) == adjacentBlockState.getValue(StairsBlock.HALF) &&
                state.getValue(StairsBlock.SHAPE) != StairsShape.STRAIGHT) {
                if (adjacentBlockState.getValue(StairsBlock.FACING) ==
                    state.getValue(StairsBlock.FACING).getCounterClockWise() &&
                    adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairsBlock.FACING) ==
                           state.getValue(StairsBlock.FACING).getClockWise() &&
                           adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
                    return true;
                }
            }
        }

        if (side == state.getValue(StairsBlock.FACING).getOpposite()) {
            if (state.getValue(StairsBlock.HALF) == adjacentBlockState.getValue(StairsBlock.HALF)) {
                if (adjacentBlockState.getValue(StairsBlock.FACING) ==
                    state.getValue(StairsBlock.FACING).getCounterClockWise() &&
                    adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairsBlock.FACING) ==
                           state.getValue(StairsBlock.FACING).getClockWise() &&
                           adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
                    return true;
                }
            }
        }

        if (side == state.getValue(StairsBlock.FACING).getCounterClockWise()) {
            if (state.getValue(StairsBlock.HALF) == adjacentBlockState.getValue(StairsBlock.HALF)) {
                if (adjacentBlockState.getValue(StairsBlock.FACING) == side &&
                    state.getValue(StairsBlock.SHAPE) != StairsShape.INNER_LEFT &&
                    adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_RIGHT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING) &&
                           adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_LEFT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairsBlock.FACING) ==
                           state.getValue(StairsBlock.FACING).getOpposite() &&
                           state.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_RIGHT) {
                    return true;
                }
            }
        }

        if (side == state.getValue(StairsBlock.FACING).getClockWise()) {
            if (state.getValue(StairsBlock.HALF) == adjacentBlockState.getValue(StairsBlock.HALF)) {
                if (adjacentBlockState.getValue(StairsBlock.FACING) == side &&
                    state.getValue(StairsBlock.SHAPE) != StairsShape.INNER_RIGHT &&
                    adjacentBlockState.getValue(StairsBlock.SHAPE) == StairsShape.INNER_LEFT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairsBlock.FACING) == state.getValue(StairsBlock.FACING) &&
                           adjacentBlockState.getValue(StairsBlock.SHAPE) != StairsShape.OUTER_RIGHT) {
                    return true;
                } else if (adjacentBlockState.getValue(StairsBlock.FACING) ==
                           state.getValue(StairsBlock.FACING).getOpposite() &&
                           state.getValue(StairsBlock.SHAPE) == StairsShape.OUTER_LEFT) {
                    return true;
                }
            }
        }

        return false;
    }
}
