package platinpython.rgbblocks.client.renderer.entity.state;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;

public class RGBFallingBlockRenderState extends EntityRenderState {
    public final RGBMovingBlockRenderState movingBlockRenderState = new RGBMovingBlockRenderState();
    public int color;

    public class RGBMovingBlockRenderState extends MovingBlockRenderState {
        @Override
        public int getBlockTint(BlockPos pos, ColorResolver color) {
            return RGBFallingBlockRenderState.this.color;
        }
    }
}
