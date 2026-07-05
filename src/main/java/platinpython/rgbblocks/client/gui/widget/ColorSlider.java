package platinpython.rgbblocks.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import platinpython.rgbblocks.client.gui.ScreenUtils;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.Color;

import java.util.Locale;
import java.util.function.Function;

public class ColorSlider extends AbstractSliderButton {
    private final SliderType type;

    private final double minValue;
    private final double maxValue;

    private final Component displayText;

    public ColorSlider(
        int x,
        int y,
        int width,
        int height,
        Component displayText,
        double minValue,
        double maxValue,
        double currentValue,
        SliderType type
    ) {
        super(x, y, width, height, displayText, (currentValue - minValue) / (maxValue - minValue));
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = (currentValue - this.minValue) / (this.maxValue - this.minValue);
        this.displayText = displayText;
        setMessage(
            Component.empty()
                .append(displayText)
                .append(Integer.toString((int) Math.round(this.value * (maxValue - minValue) + minValue)))
        );
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.empty().append(this.displayText).append(Integer.toString(getValueInt())));
    }

    @Override
    protected void applyValue() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof ColorSelectScreen screen && screen.hexBox != null && screen.redSlider != null
            && screen.greenSlider != null && screen.blueSlider != null) {
            screen.hexBox.setValue(
                "#" + Integer
                    .toHexString(
                        new Color(
                            screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(),
                            screen.blueSlider.getValueInt()
                        ).getRGB()
                    )
                    .substring(2)
                    .toUpperCase(Locale.ENGLISH)
            );
        }
    }

    public int getValueInt() {
        return (int) Math.round(this.value * (maxValue - minValue) + minValue);
    }

    public double getValue() {
        return this.value * (maxValue - minValue) + minValue;
    }

    public void setValueInt(int value) {
        this.value = (value - this.minValue) / (this.maxValue - this.minValue);
        this.updateMessage();
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            Minecraft minecraft = Minecraft.getInstance();
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF000000);
            if (minecraft.screen instanceof ColorSelectScreen screen) {
                switch (type) {
                    case RED:
                        renderRedBackground(graphics, screen);
                        break;
                    case GREEN:
                        renderGreenBackground(graphics, screen);
                        break;
                    case BLUE:
                        renderBlueBackground(graphics, screen);
                        break;
                    case HUE:
                        renderHueBackground(graphics, screen);
                        break;
                    case SATURATION:
                        renderSaturationBackground(graphics, screen);
                        break;
                    case BRIGHTNESS:
                        renderBrightnessBackground(graphics, screen);
                        break;
                }
            }

            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED, this.getHandleSprite(),
                (int) (this.getX() + (this.value * (this.width - 8))), this.getY(), 8, this.getHeight()
            );

            graphics.centeredText(
                minecraft.font, this.getMessage(), this.getX() + this.width / 2, this.getY() - this.height / 10 * 7,
                getFGColor()
            );
        }
    }

    private void renderRedBackground(GuiGraphicsExtractor graphics, ColorSelectScreen screen) {
        if (screen.greenSlider == null || screen.blueSlider == null) {
            return;
        }
        int leftColor = new Color(0x00, screen.greenSlider.getValueInt(), screen.blueSlider.getValueInt()).getRGB();
        int rightColor = new Color(0xFF, screen.greenSlider.getValueInt(), screen.blueSlider.getValueInt()).getRGB();
        ScreenUtils.fillGradient(
            graphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1,
            leftColor, rightColor
        );
    }

    private void renderGreenBackground(GuiGraphicsExtractor graphics, ColorSelectScreen screen) {
        if (screen.redSlider == null || screen.blueSlider == null) {
            return;
        }
        int leftColor = new Color(screen.redSlider.getValueInt(), 0x00, screen.blueSlider.getValueInt()).getRGB();
        int rightColor = new Color(screen.redSlider.getValueInt(), 0xFF, screen.blueSlider.getValueInt()).getRGB();
        ScreenUtils.fillGradient(
            graphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1,
            leftColor, rightColor
        );
    }

    private void renderBlueBackground(GuiGraphicsExtractor graphics, ColorSelectScreen screen) {
        if (screen.redSlider == null || screen.greenSlider == null) {
            return;
        }
        int leftColor = new Color(screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(), 0x00).getRGB();
        int rightColor = new Color(screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(), 0xFF).getRGB();
        ScreenUtils.fillGradient(
            graphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1,
            leftColor, rightColor
        );
    }

    private void renderHueBackground(GuiGraphicsExtractor graphics, ColorSelectScreen screen) {
        if (screen.saturationSlider == null || screen.brightnessSlider == null) {
            return;
        }
        Function<Integer, Integer> lerp =
            (pct) -> (int) Math.floor(Mth.lerp(pct / 100f, this.getX() + 1, this.getX() + this.width - 1));
        Function<Integer, Integer> color = (pct) -> Color.HSBtoRGB(
            (pct / 100f), (float) (screen.saturationSlider.getValueInt() / ColorSelectScreen.MAX_VALUE_SB),
            (float) (screen.brightnessSlider.getValueInt() / ColorSelectScreen.MAX_VALUE_SB)
        );
        ScreenUtils.fillGradient(
            graphics, lerp.apply(0), this.getY() + 1, lerp.apply(17), this.getY() + this.height - 1, color.apply(0),
            color.apply(17)
        );
        ScreenUtils.fillGradient(
            graphics, lerp.apply(17), this.getY() + 1, lerp.apply(34), this.getY() + this.height - 1, color.apply(17),
            color.apply(34)
        );
        ScreenUtils.fillGradient(
            graphics, lerp.apply(34), this.getY() + 1, lerp.apply(50), this.getY() + this.height - 1, color.apply(34),
            color.apply(50)
        );
        ScreenUtils.fillGradient(
            graphics, lerp.apply(50), this.getY() + 1, lerp.apply(66), this.getY() + this.height - 1, color.apply(50),
            color.apply(66)
        );
        ScreenUtils.fillGradient(
            graphics, lerp.apply(66), this.getY() + 1, lerp.apply(82), this.getY() + this.height - 1, color.apply(66),
            color.apply(82)
        );
        ScreenUtils.fillGradient(
            graphics, lerp.apply(82), this.getY() + 1, lerp.apply(100), this.getY() + this.height - 1, color.apply(82),
            color.apply(100)
        );
    }

    private void renderSaturationBackground(GuiGraphicsExtractor graphics, ColorSelectScreen screen) {
        if (screen.hueSlider == null || screen.brightnessSlider == null) {
            return;
        }
        int leftColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE), 0.0f,
            (float) (screen.brightnessSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB)
        );
        int rightColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE), 1.0f,
            (float) (screen.brightnessSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB)
        );
        ScreenUtils.fillGradient(
            graphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1,
            leftColor, rightColor
        );
    }

    private void renderBrightnessBackground(GuiGraphicsExtractor graphics, ColorSelectScreen screen) {
        if (screen.hueSlider == null || screen.saturationSlider == null) {
            return;
        }
        int leftColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE),
            (float) (screen.saturationSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB), 0.0f
        );
        int rightColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE),
            (float) (screen.saturationSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB), 1.0f
        );
        ScreenUtils.fillGradient(
            graphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1,
            leftColor, rightColor
        );
    }
}
