package platinpython.rgbblocks.block;

import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class RGBConcreteBlock extends RGBBlock {
    public RGBConcreteBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean shouldChangedStateKeepBlockEntity(BlockState oldState) {
        return oldState.is(BlockRegistry.RGB_CONCRETE_POWDER);
    }
}
