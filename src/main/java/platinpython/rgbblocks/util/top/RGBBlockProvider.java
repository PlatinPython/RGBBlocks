package platinpython.rgbblocks.util.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.Color;

public class RGBBlockProvider implements IProbeInfoProvider {
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level world, BlockState state,
                             IProbeHitData hitData) {
        BlockEntity tileEntity = world.getBlockEntity(hitData.getPos());
        if (tileEntity instanceof RGBTileEntity) {
            if (mode == ProbeMode.NORMAL) {
                info.text("#" + Integer.toHexString(((RGBTileEntity) tileEntity).getColor()).substring(2));
            }

            if (mode == ProbeMode.EXTENDED) {
                Color color = new Color(((RGBTileEntity) tileEntity).getColor());
                MutableComponent red = new TranslatableComponent("gui.rgbblocks.red").append(": " + color.getRed());
                MutableComponent green = new TranslatableComponent("gui.rgbblocks.green").append(": " +
                                                                                                 color.getGreen());
                MutableComponent blue = new TranslatableComponent("gui.rgbblocks.blue").append(": " + color.getBlue());
                info.text(red.append(", ").append(green).append(", ").append(blue));
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
                MutableComponent hue = new TranslatableComponent("gui.rgbblocks.hue").append(": " +
                                                                                             Math.round(hsb[0] *
                                                                                                        ColorSelectScreen.MAX_VALUE_HUE));
                MutableComponent saturation = new TranslatableComponent("gui.rgbblocks.saturation").append(": " +
                                                                                                           Math.round(
                                                                                                                   hsb[1] *
                                                                                                                   ColorSelectScreen.MAX_VALUE_SB));
                MutableComponent brightness = new TranslatableComponent("gui.rgbblocks.brightness").append(": " +
                                                                                                           Math.round(
                                                                                                                   hsb[2] *
                                                                                                                   ColorSelectScreen.MAX_VALUE_SB));
                info.text(hue.append("\u00B0, ").append(saturation).append("%, ").append(brightness).append("%"));
            }
        }
    }

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(RGBBlocks.MOD_ID, "block");
    }
}
