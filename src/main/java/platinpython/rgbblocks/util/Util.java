package platinpython.rgbblocks.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

import java.util.HexFormat;
import java.util.function.Consumer;

public class Util {
    private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();

    public static void appendHoverText(ItemStack stack, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        int color = stack.getOrDefault(DataComponentRegistry.COLOR, -1);
        if (tooltipFlag.hasShiftDown()) {
            builder.accept(fullRGBColorComponent(color));
            builder.accept(fullHSBColorComponent(color));
        } else {
            builder.accept(hexColorComponent(color));
        }
    }

    public static Component hexColorComponent(int color) {
        return Component.literal("#" + HEX_FORMAT.toHexDigits(color).substring(2));
    }

    public static Component fullRGBColorComponent(int color) {
        MutableComponent red = Component.translatable("gui.rgbblocks.red").append(": " + ARGB.red(color));
        MutableComponent green = Component.translatable("gui.rgbblocks.green").append(": " + ARGB.green(color));
        MutableComponent blue = Component.translatable("gui.rgbblocks.blue").append(": " + ARGB.blue(color));
        return red.append(", ").append(green).append(", ").append(blue);
    }

    public static Component fullHSBColorComponent(int color) {
        float[] hsb = Color.RGBtoHSB(ARGB.red(color), ARGB.green(color), ARGB.blue(color));
        MutableComponent hue = Component.translatable("gui.rgbblocks.hue")
            .append(": " + Math.round(hsb[0] * ColorSelectScreen.MAX_VALUE_HUE));
        MutableComponent saturation = Component.translatable("gui.rgbblocks.saturation")
            .append(": " + Math.round(hsb[1] * ColorSelectScreen.MAX_VALUE_SB));
        MutableComponent brightness = Component.translatable("gui.rgbblocks.brightness")
            .append(": " + Math.round(hsb[2] * ColorSelectScreen.MAX_VALUE_SB));
        return hue.append("°, ").append(saturation).append("%, ").append(brightness).append("%");
    }
}
