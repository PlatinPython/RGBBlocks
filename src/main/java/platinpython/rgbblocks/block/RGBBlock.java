package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

public class RGBBlock extends Block implements EntityBlock {
    public RGBBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RGBBlockUtils.newBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(
        LevelReader level,
        BlockPos pos,
        BlockState state,
        boolean includeData,
        Player player
    ) {
        return RGBBlockUtils.getCloneItemStack(state, level, pos);
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        return RGBBlockUtils.getMapColor(level, pos, defaultColor);
    }
}
