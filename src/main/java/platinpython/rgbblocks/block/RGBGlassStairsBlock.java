package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class RGBGlassStairsBlock extends RGBStairsBlock {
    public RGBGlassStairsBlock(Properties properties) {
        super(BlockRegistry.RGB_GLASS.get().defaultBlockState(), properties);
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
        return RGBBlockUtils.stairSkipRendering(state, adjacentBlockState, side);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }
}
