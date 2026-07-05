package platinpython.rgbblocks.util.compat.framedblocks;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerClientHandler;
import io.github.xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class RGBBlocksCamoContainerClientHandler
    extends CamoContainerClientHandler<BlockCamoContent, RGBBlocksCamoContainer> {
    public static final CamoContainerClientHandler<BlockCamoContent, RGBBlocksCamoContainer> INSTANCE =
        new RGBBlocksCamoContainerClientHandler();

    @Override
    public int getTintCount(RGBBlocksCamoContainer camoContainer) {
        return 1;
    }

    @Override
    public void collectTintValues(
        RGBBlocksCamoContainer camoContainer,
        BlockAndTintGetter level,
        BlockPos pos,
        IntList tintList
    ) {
        tintList.add(camoContainer.color);
    }

    @Override
    public void collectTintValues(RGBBlocksCamoContainer camoContainer, ItemStack stack, IntList tintList) {
        tintList.add(camoContainer.color);
    }
}
