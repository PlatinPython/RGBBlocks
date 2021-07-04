package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RGBRedstoneLampBlock extends RedstoneLampBlock implements RGBBlock {
	public RGBRedstoneLampBlock() {
		super(Properties.copy(Blocks.REDSTONE_LAMP));
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
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return RGBBlock.getPickBlock(state, target, world, pos, player);
	}
}
