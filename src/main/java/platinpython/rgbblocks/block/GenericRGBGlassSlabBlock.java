package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class GenericRGBGlassSlabBlock extends GenericRGBSlabBlock implements GlassBlock, GlassSlabBlock {
	public GenericRGBGlassSlabBlock() {
		super(Properties.copy(Blocks.GLASS));
	}
	
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
		return GlassBlock.getBeaconColorMultiplier(state, world, pos, beaconPos);
	}
	
	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return GlassSlabBlock.skipRendering(state, adjacentBlockState, side);
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
