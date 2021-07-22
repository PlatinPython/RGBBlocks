package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class RGBCarpetBlock extends WoolCarpetBlock implements EntityBlock {
	public RGBCarpetBlock() {
		super(DyeColor.WHITE, Properties.copy(Blocks.WHITE_CARPET));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return RGBBlockUtils.newBlockEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		RGBBlockUtils.setPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return RGBBlockUtils.getPickBlock(state, target, world, pos, player);
	}
}
