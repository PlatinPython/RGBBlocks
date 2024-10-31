package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class RGBGlassSlabBlock extends RGBSlabBlock {
    public RGBGlassSlabBlock() {
        super(Properties.ofFullCopy(Blocks.GLASS));
    }

    @Override
    public @Nullable Integer getBeaconColorMultiplier(
        BlockState state,
        LevelReader level,
        BlockPos pos,
        BlockPos beaconPos
    ) {
        return RGBBlockUtils.getBeaconColorMultiplier(level, pos);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return RGBBlockUtils.slabSkipRendering(state, adjacentBlockState, side);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
}
