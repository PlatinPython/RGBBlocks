package platinpython.rgbblocks.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class RGBConcretePowderBlock extends ConcretePowderBlock implements IRGBBlock {
//	TileEntity tileEntity;
	public RGBConcretePowderBlock() {
		super(BlockRegistry.RGB_CONCRETE.get(), Properties.copy(Blocks.WHITE_CONCRETE_POWDER));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return IRGBBlock.hasTileEntity(state);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return IRGBBlock.createTileEntity(state, world);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		IRGBBlock.setPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		return IRGBBlock.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (worldIn.isEmptyBlock(pos.below()) || isFree(worldIn.getBlockState(pos.below())) && pos.getY() >= 0) {
			RGBFallingBlockEntity fallingBlockEntity = new RGBFallingBlockEntity(worldIn, (double) pos.getX() + 0.5D,
					(double) pos.getY(), (double) pos.getZ() + 0.5D, worldIn.getBlockState(pos), pos);
			this.falling(fallingBlockEntity);
			worldIn.addFreshEntity(fallingBlockEntity);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() == BlockRegistry.RGB_CONCRETE_POWDER.get()
				&& newState.getBlock() == BlockRegistry.RGB_CONCRETE.get()) {
		} else {
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}
}
