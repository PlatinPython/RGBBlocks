package platinpython.rgbblocks.util.compat.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.Color;

import java.util.HexFormat;

public class RGBBlockProvider implements IProbeInfoProvider {
    private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();

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
            if (mode == ProbeMode.NORMAL) {
                info.text("#" + HEX_FORMAT.toHexDigits(rgbBlockEntity.getColor()).substring(2));
            }

            if (mode == ProbeMode.EXTENDED) {
                Color color = new Color(rgbBlockEntity.getColor());
                MutableComponent red = Component.translatable("gui.rgbblocks.red").append(": " + color.getRed());
                MutableComponent green = Component.translatable("gui.rgbblocks.green").append(": " + color.getGreen());
                MutableComponent blue = Component.translatable("gui.rgbblocks.blue").append(": " + color.getBlue());
                info.text(red.append(", ").append(green).append(", ").append(blue));
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
                MutableComponent hue = Component.translatable("gui.rgbblocks.hue")
                    .append(": " + Math.round(hsb[0] * ColorSelectScreen.MAX_VALUE_HUE));
                MutableComponent saturation = Component.translatable("gui.rgbblocks.saturation")
                    .append(": " + Math.round(hsb[1] * ColorSelectScreen.MAX_VALUE_SB));
                MutableComponent brightness = Component.translatable("gui.rgbblocks.brightness")
                    .append(": " + Math.round(hsb[2] * ColorSelectScreen.MAX_VALUE_SB));
                info.text(hue.append("°, ").append(saturation).append("%, ").append(brightness).append("%"));
            }
        }
    }

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(RGBBlocks.MOD_ID, "block");
    }
}
