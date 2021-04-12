package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class GenericRGBGlassStairsBlock extends GenericRGBStairsBlock implements IGlassBlock {
	public GenericRGBGlassStairsBlock(java.util.function.Supplier<BlockState> state, Properties properties) {
		super(state, properties);
	}
	
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
		return IGlassBlock.getBeaconColorMultiplier(state, world, pos, beaconPos);
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return IGlassBlock.isSideInvisible(state, adjacentBlockState, side);
	}
}
