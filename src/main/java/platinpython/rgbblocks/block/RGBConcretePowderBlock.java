package platinpython.rgbblocks.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class RGBConcretePowderBlock extends ConcretePowderBlock implements RGBBlock {
	public RGBConcretePowderBlock() {
		super(BlockRegistry.RGB_CONCRETE.get(), Properties.copy(Blocks.WHITE_CONCRETE_POWDER));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return RGBBlock.hasTileEntity(state);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RGBBlock.createTileEntity(state, world);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		RGBBlock.setPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		return RGBBlock.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (worldIn.isEmptyBlock(pos.below()) || isFree(worldIn.getBlockState(pos.below())) && pos.getY() >= 0) {
			TileEntity tileEntity = worldIn.getBlockEntity(pos);
			RGBFallingBlockEntity fallingBlockEntity = new RGBFallingBlockEntity(worldIn, (double) pos.getX() + 0.5D,
					(double) pos.getY(), (double) pos.getZ() + 0.5D,
					tileEntity instanceof RGBTileEntity ? ((RGBTileEntity) tileEntity).getColor() : 0);
			try {
				RGBFallingBlockEntity.blockState.set(fallingBlockEntity, worldIn.getBlockState(pos));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			this.falling(fallingBlockEntity);
			worldIn.addFreshEntity(fallingBlockEntity);
		}
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(BlockRegistry.RGB_CONCRETE_POWDER.get()) && !newState.is(BlockRegistry.RGB_CONCRETE.get())) {
			if (state.hasTileEntity() && (!state.is(newState.getBlock()) || !newState.hasTileEntity())) {
				worldIn.removeBlockEntity(pos);
			}
		}
	}

	@Override
	public void onLand(World world, BlockPos blockPos, BlockState blockBlockState, BlockState entityBlockState,
			FallingBlockEntity entity) {
		super.onLand(world, blockPos, blockBlockState, entityBlockState, entity);
		if (entity instanceof RGBFallingBlockEntity) {
			RGBTileEntity tileEntity = new RGBTileEntity();
			tileEntity.setColor(((RGBFallingBlockEntity) entity).getColor());
			world.setBlockEntity(blockPos, tileEntity);
		}
	}

	@Override
	public int getDustColor(BlockState blockState, IBlockReader blockReader, BlockPos blockPos) {
		if (blockReader != null) {
			TileEntity tileEntity = blockReader.getBlockEntity(blockPos.above());
			if (tileEntity instanceof RGBTileEntity) {
				return ((RGBTileEntity) tileEntity).getColor();
			}
		}
		return super.getDustColor(blockState, blockReader, blockPos);
	}
}
