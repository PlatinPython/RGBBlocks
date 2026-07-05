package platinpython.rgbblocks.util.compat.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.util.Util;

public class RGBBlockProvider implements IProbeInfoProvider {
    @Override
    public void addProbeInfo(
        ProbeMode mode,
        IProbeInfo info,
        Player player,
        Level level,
        BlockState state,
        IProbeHitData hitData
    ) {
        BlockEntity blockEntity = level.getBlockEntity(hitData.getPos());
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            int color = rgbBlockEntity.getColor();
            if (mode == ProbeMode.NORMAL) {
                info.text(Util.hexColorComponent(color));
            }

            if (mode == ProbeMode.EXTENDED) {
                info.text(Util.fullRGBColorComponent(color));
                info.text(Util.fullHSBColorComponent(color));
            }
        }
    }

    @Override
    public ResourceLocation getID() {
        return ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "block");
    }
}
