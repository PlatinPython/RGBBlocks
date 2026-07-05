package platinpython.rgbblocks.client.renderer.entity.state;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class RGBFallingBlockRenderState extends EntityRenderState {
    public final RGBMovingBlockRenderState movingBlockRenderState = new RGBMovingBlockRenderState();
    public int color;

    public class RGBMovingBlockRenderState extends MovingBlockRenderState {
        public int color() {
            return RGBFallingBlockRenderState.this.color;
        }
    }
}
