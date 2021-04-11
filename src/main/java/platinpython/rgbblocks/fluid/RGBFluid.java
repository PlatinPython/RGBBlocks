package platinpython.rgbblocks.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class RGBFluid extends Fluid{
	public RGBFluid() {
	}

	@Override
	public Item getFilledBucket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean canDisplace(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_,
			Fluid p_215665_4_, Direction p_215665_5_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Vec3d getFlow(IBlockReader p_215663_1_, BlockPos p_215663_2_, IFluidState p_215663_3_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTickRate(IWorldReader p_205569_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected float getExplosionResistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getActualHeight(IFluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getHeight(IFluidState p_223407_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected BlockState getBlockState(IFluidState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSource(IFluidState state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLevel(IFluidState p_207192_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public VoxelShape func_215664_b(IFluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_) {
		// TODO Auto-generated method stub
		return null;
	}

}
