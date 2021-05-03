package platinpython.rgbblocks.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class RGBFluid extends FlowingFluid {

	@Override
	public Fluid getFlowing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fluid getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean canConvertToSource() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(IWorld world, BlockPos pos, BlockState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getSlopeFindDistance(IWorldReader reader) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getDropOff(IWorldReader reader) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Item getBucket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, IBlockReader reader, BlockPos pos,
			Fluid fluid, Direction direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTickDelay(IWorldReader reader) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected float getExplosionResistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSource(FluidState state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAmount(FluidState state) {
		// TODO Auto-generated method stub
		return 0;
	}
}
