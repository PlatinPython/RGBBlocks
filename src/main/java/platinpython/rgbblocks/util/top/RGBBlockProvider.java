package platinpython.rgbblocks.util.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.Color;

public class RGBBlockProvider implements IProbeInfoProvider {
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, PlayerEntity player, World world, BlockState state,
                             IProbeHitData hitData) {
        TileEntity tileEntity = world.getBlockEntity(hitData.getPos());
        if (tileEntity instanceof RGBTileEntity) {
            if (mode == ProbeMode.NORMAL) {
                info.text("#" + Integer.toHexString(((RGBTileEntity) tileEntity).getColor()).substring(2));
            }

            if (mode == ProbeMode.EXTENDED) {
                Color color = new Color(((RGBTileEntity) tileEntity).getColor());
                IFormattableTextComponent red = new TranslationTextComponent("gui.rgbblocks.red").append(": " +
                                                                                                         color.getRed());
                IFormattableTextComponent green = new TranslationTextComponent("gui.rgbblocks.green").append(": " +
                                                                                                             color.getGreen());
                IFormattableTextComponent blue = new TranslationTextComponent("gui.rgbblocks.blue").append(": " +
                                                                                                           color.getBlue());
                info.text(red.append(", ").append(green).append(", ").append(blue));
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
                IFormattableTextComponent hue = new TranslationTextComponent("gui.rgbblocks.hue").append(": " +
                                                                                                         Math.round(hsb[0] *
                                                                                                                    ColorSelectScreen.MAX_VALUE_HUE));
                IFormattableTextComponent saturation = new TranslationTextComponent("gui.rgbblocks.saturation").append(
                        ": " + Math.round(hsb[1] * ColorSelectScreen.MAX_VALUE_SB));
                IFormattableTextComponent brightness = new TranslationTextComponent("gui.rgbblocks.brightness").append(
                        ": " + Math.round(hsb[2] * ColorSelectScreen.MAX_VALUE_SB));
                info.text(hue.append("°, ").append(saturation).append("%, ").append(brightness).append("%"));
            }
        }
    }

    @Override
    public String getID() {
        return RGBBlocks.MOD_ID;
    }
}
