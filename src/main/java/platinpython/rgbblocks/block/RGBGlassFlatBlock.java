package platinpython.rgbblocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public class RGBGlassFlatBlock extends Block {
	public RGBGlassFlatBlock() {
		super(Block.Properties.create(new Material.Builder(MaterialColor.AIR).build()).sound(SoundType.GLASS).notSolid());
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityRegistry.RGB.get().create();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasTag() == true) {
			RGBTileEntity tileEntity = (RGBTileEntity) worldIn.getTileEntity(pos);
			tileEntity.red = stack.getTag().getInt("red");
			tileEntity.green = stack.getTag().getInt("green");
			tileEntity.blue = stack.getTag().getInt("blue");
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		ItemStack stack = new ItemStack(this.asItem());
		RGBTileEntity tileEntity = (RGBTileEntity) world.getTileEntity(pos);
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("red", tileEntity.red);
		tag.putInt("green", tileEntity.green);
		tag.putInt("blue", tileEntity.blue);
		stack.setTag(tag);
		return stack;
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
		RGBTileEntity tileEntity = (RGBTileEntity) world.getTileEntity(pos);
		CompoundNBT compound = tileEntity.getUpdateTag();
		return new float[] { (float) compound.getInt("red") / 255.0F, (float) compound.getInt("green") / 255.0F,
				(float) compound.getInt("blue") / 255.0F };
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.getBlock() instanceof RGBGlassFlatBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatBlock) {
			return true;
		} else if (adjacentBlockState.getBlock() instanceof RGBGlassFlatSlabBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatSlabBlock) {
			return isInvisibleToGlassSlab(state, adjacentBlockState, side);
		} else if (adjacentBlockState.getBlock() instanceof RGBGlassFlatStairsBlock
				|| adjacentBlockState.getBlock() instanceof RGBLampGlassFlatStairsBlock) {
			return isInvisibleToGlassStairs(state, adjacentBlockState, side);
		} else {
			return false;
		}
	}

	private boolean isInvisibleToGlassSlab(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
			return true;
		}

		switch (side) {
		case UP:
		case DOWN:
			return (state.get(SlabBlock.TYPE) != adjacentBlockState.get(SlabBlock.TYPE));
		case NORTH:
		case EAST:
		case SOUTH:
		case WEST:
			return (state.get(SlabBlock.TYPE) == adjacentBlockState.get(SlabBlock.TYPE));
		}

		return false;
	}

	private boolean isInvisibleToGlassStairs(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (side == Direction.UP && adjacentBlockState.get(StairsBlock.HALF) == Half.BOTTOM) {
			return true;
		}

		if (side == Direction.DOWN && adjacentBlockState.get(StairsBlock.HALF) == Half.TOP) {
			return true;
		}

		if (adjacentBlockState.get(StairsBlock.FACING) == side.getOpposite()) {
			return true;
		}

		if (side.getHorizontalIndex() != -1) {
			if (state.get(SlabBlock.TYPE) == SlabType.BOTTOM
					&& adjacentBlockState.get(StairsBlock.HALF) == Half.BOTTOM) {
				return true;
			} else if (state.get(SlabBlock.TYPE) == SlabType.TOP
					&& adjacentBlockState.get(StairsBlock.HALF) == Half.TOP) {
				return true;
			}
		}

		return false;
	}
}
