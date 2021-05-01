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
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class RGBConcretePowderBlock extends ConcretePowderBlock implements IRGBBlock {
	public RGBConcretePowderBlock() {
		super(BlockRegistry.RGB_CONCRETE.get(), Properties.from(Blocks.WHITE_CONCRETE_POWDER));
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
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		IRGBBlock.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		return IRGBBlock.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down())) && pos.getY() >= 0) {
//			CompoundNBT tag = worldIn.getTileEntity(pos).getUpdateTag();
//			red = tag.getInt("red");
//			green = tag.getInt("green");
//			blue = tag.getInt("blue");
			FallingBlockEntity fallingblockentity = new FallingBlockEntity(worldIn, (double) pos.getX() + 0.5D,
					(double) pos.getY(), (double) pos.getZ() + 0.5D, worldIn.getBlockState(pos));
			this.onStartFalling(fallingblockentity);

			// Deactivated until I can find a method to keep the color of the block
			// throughout the fall and after the fall

			// worldIn.addEntity(fallingblockentity);
		}
	}

	@Override
	public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState) {
		super.onEndFalling(worldIn, pos, fallingState, hitState);
//		CompoundNBT tag = worldIn.getTileEntity(pos).getUpdateTag();
//		tag.remove("red");
//		tag.remove("green");
//		tag.remove("blue");
//		tag.putInt("red", red);
//		tag.putInt("green", green);
//		tag.putInt("blue", blue);
//		worldIn.notifyBlockUpdate(pos, fallingState, hitState, -1);
	}

	// Currently needed as particle creation seems to crash the game

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		// super.animateTick(stateIn, worldIn, pos, rand);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasTileEntity() && (newState.getBlock() == BlockRegistry.RGB_CONCRETE.get())) {
		} else {
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}
}
