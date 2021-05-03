package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class GenericRGBStairsBlock extends StairsBlock implements IRGBBlock {
	public GenericRGBStairsBlock(java.util.function.Supplier<BlockState> state, Properties properties) {
		super(state, properties);
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
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
}
