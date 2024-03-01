package platinpython.rgbblocks.client.colorhandlers;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class RGBBlockColor implements BlockColor {
    public int getColor(
        BlockState blockState,
        @Nullable BlockAndTintGetter blockDisplayReader,
        @Nullable BlockPos blockPos,
        int tintindex
    ) {
        if (blockDisplayReader == null || blockPos == null) {
            return -1;
        }
        BlockEntity tileEntity = blockDisplayReader.getBlockEntity(blockPos);
        if (tileEntity == null) {
            tileEntity = blockDisplayReader.getBlockEntity(blockPos.below());
            if (tileEntity == null) {
                return -1;
            }
        }
        if (tileEntity instanceof RGBTileEntity rgbTileEntity) {
            return rgbTileEntity.getColor();
        } else {
            return -1;
        }
    }
}
