package platinpython.rgbblocks.block;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class RGBAntiblockBlock extends GenericRGBBlock {
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.EAST, EAST);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.WEST, WEST);
		map.put(Direction.UP, UP);
		map.put(Direction.DOWN, DOWN);
	});

	public RGBAntiblockBlock() {
		super(Properties.copy(Blocks.STONE));
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getStateForPlacement(context.getLevel(), context.getClickedPos(), context.getItemInHand().getTag().getInt("color"));
	}

	public BlockState getStateForPlacement(IBlockReader level, BlockPos pos, int color) {
		Integer colorNorth = level.getBlockState(pos.north()).getBlock() instanceof RGBAntiblockBlock ? ((RGBTileEntity) level.getBlockEntity(pos.north())).getColor() : null;
		Integer colorEast = level.getBlockState(pos.east()).getBlock() instanceof RGBAntiblockBlock ? ((RGBTileEntity) level.getBlockEntity(pos.east())).getColor() : null;
		Integer colorSouth = level.getBlockState(pos.south()).getBlock() instanceof RGBAntiblockBlock ? ((RGBTileEntity) level.getBlockEntity(pos.south())).getColor() : null;
		Integer colorWest = level.getBlockState(pos.west()).getBlock() instanceof RGBAntiblockBlock ? ((RGBTileEntity) level.getBlockEntity(pos.west())).getColor() : null;
		Integer colorUp = level.getBlockState(pos.above()).getBlock() instanceof RGBAntiblockBlock ? ((RGBTileEntity) level.getBlockEntity(pos.above())).getColor() : null;
		Integer colorDown = level.getBlockState(pos.below()).getBlock() instanceof RGBAntiblockBlock ? ((RGBTileEntity) level.getBlockEntity(pos.below())).getColor() : null;
		return this.defaultBlockState().setValue(NORTH, Boolean.valueOf(colorNorth != null && colorNorth == color)).setValue(EAST, Boolean.valueOf(colorEast != null && colorEast == color)).setValue(SOUTH, Boolean.valueOf(colorSouth != null && colorSouth == color)).setValue(WEST, Boolean.valueOf(colorWest != null && colorWest == color)).setValue(UP, Boolean.valueOf(colorUp != null && colorUp == color)).setValue(DOWN, Boolean.valueOf(colorDown != null && colorDown == color));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState adjacentState, IWorld level, BlockPos pos, BlockPos adjacentPos) {
		return state.setValue(PROPERTY_BY_DIRECTION.get(facing), Boolean.valueOf(adjacentState.getBlock() instanceof RGBAntiblockBlock && ((RGBTileEntity) level.getBlockEntity(adjacentPos)).getColor() == ((RGBTileEntity) level.getBlockEntity(pos)).getColor()));
	}
}
