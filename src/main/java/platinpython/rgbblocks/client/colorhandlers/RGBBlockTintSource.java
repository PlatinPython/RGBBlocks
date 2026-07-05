package platinpython.rgbblocks.client.colorhandlers;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;

public class RGBBlockTintSource implements BlockTintSource {
    @Override
    public int color(BlockState state) {
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof RGBBlockEntity blockEntity) {
            return blockEntity.getColor();
        }
        return this.color(state);
    }
}
