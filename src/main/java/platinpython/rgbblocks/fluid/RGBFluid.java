package platinpython.rgbblocks.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public class RGBFluid extends FlowingFluid {

	@Override
	public Fluid getFlowingFluid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fluid getStillFluid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean canSourcesMultiply() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getSlopeFindDistance(IWorldReader worldIn) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Item getFilledBucket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean canDisplace(IFluidState fluidStateIn, IBlockReader blockReader, BlockPos pos, Fluid fluidIn,
			Direction directionIn) {
		// TODO Auto-generated method stub
		return false;
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
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(new ResourceLocation("rgb_glass_flat"), new ResourceLocation("rgb_glass_flat")).build(getFlowingFluid());
	}
}
