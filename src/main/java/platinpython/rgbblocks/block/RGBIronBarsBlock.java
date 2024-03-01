package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class RGBIronBarsBlock extends IronBarsBlock implements EntityBlock {
    public RGBIronBarsBlock(Properties properties) {
        super(properties);
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
    public ItemStack getCloneItemStack(
        BlockState state,
        HitResult target,
        BlockGetter world,
        BlockPos pos,
        Player player
    ) {
        return RGBBlockUtils.getCloneItemStack(state, target, world, pos, player);
    }
}
