package platinpython.rgbblocks.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.gui.widget.ColorSlider;
import platinpython.rgbblocks.client.gui.widget.SliderType;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.network.packets.PaintBucketSyncPKT;

import java.util.Locale;
import java.util.function.UnaryOperator;

public class ColorSelectScreen extends Screen {
    private double red, green, blue;
    public ColorSlider redSlider, greenSlider, blueSlider;
    private double hue, saturation, brightness;
    public ColorSlider hueSlider, saturationSlider, brightnessSlider;
    public EditBox hexBox;

    public final int WIDGET_HEIGHT = 20;
    public final int SLIDER_WIDTH = 310;
    public final int BUTTON_WIDTH = 98;
    public final int BOX_WIDTH = 50;
    public final int SPACING = WIDGET_HEIGHT + 5;

    public static final double MIN_VALUE = 0.0D;
    public static final double MAX_VALUE_RGB = 255.0D;
    public static final double MAX_VALUE_HUE = 360.0D;
    public static final double MAX_VALUE_SB = 100.0D;

    private boolean isRGBSelected;

    private final Component useRGBText, useHSBText;

    public ColorSelectScreen(int colorIn, boolean isRGBSelected) {
        super(Component.empty());
        Color color = new Color(colorIn);
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();

        float[] hsb = Color.RGBtoHSB((int) red, (int) green, (int) blue);
        this.hue = hsb[0] * MAX_VALUE_HUE;
        this.saturation = hsb[1] * MAX_VALUE_SB;
        this.brightness = hsb[2] * MAX_VALUE_SB;

        this.isRGBSelected = isRGBSelected;

        this.useRGBText = Component.translatable("gui.rgbblocks.useRGB");
        this.useHSBText = Component.translatable("gui.rgbblocks.useHSB");
    }

    public int getColor() {
        if (isRGBSelected) {
            return new Color(
                this.redSlider.getValueInt(), this.greenSlider.getValueInt(), this.blueSlider.getValueInt()
            ).getRGB();
        } else {
            return Color
                .getHSBColor(
                    (float) (hueSlider.getValueInt() / MAX_VALUE_HUE),
                    (float) (saturationSlider.getValueInt() / MAX_VALUE_SB),
                    (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB)
                )
                .getRGB();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - SLIDER_WIDTH / 2;
        int y = this.height / 2 - WIDGET_HEIGHT / 2 - SPACING;

        if (redSlider == null) {
            this.redSlider = new ColorSlider(
                x, y, SLIDER_WIDTH, WIDGET_HEIGHT, Component.translatable("gui.rgbblocks.red").append(": "), MIN_VALUE,
                MAX_VALUE_RGB, this.red, SliderType.RED
            );
        } else {
            this.redSlider.setX(x);
            this.redSlider.setY(y);
        }

        if (hueSlider == null) {
            this.hueSlider = new ColorSlider(
                x, y, SLIDER_WIDTH, WIDGET_HEIGHT, Component.translatable("gui.rgbblocks.hue").append(": "), MIN_VALUE,
                MAX_VALUE_HUE, this.hue, SliderType.HUE
            );
        } else {
            this.hueSlider.setX(x);
            this.hueSlider.setY(y);
        }

        y += SPACING + 15;

        if (greenSlider == null) {
            this.greenSlider = new ColorSlider(
                x, y, SLIDER_WIDTH, WIDGET_HEIGHT, Component.translatable("gui.rgbblocks.green").append(": "),
                MIN_VALUE, MAX_VALUE_RGB, this.green, SliderType.GREEN
            );
        } else {
            this.greenSlider.setX(x);
            this.greenSlider.setY(y);
        }

        if (saturationSlider == null) {
            this.saturationSlider = new ColorSlider(
                x, y, SLIDER_WIDTH, WIDGET_HEIGHT, Component.translatable("gui.rgbblocks.saturation").append(": "),
                MIN_VALUE, MAX_VALUE_SB, this.saturation, SliderType.SATURATION
            );
        } else {
            this.saturationSlider.setX(x);
            this.saturationSlider.setY(y);
        }

        y += SPACING + 15;

        if (blueSlider == null) {
            this.blueSlider = new ColorSlider(
                x, y, SLIDER_WIDTH, WIDGET_HEIGHT, Component.translatable("gui.rgbblocks.blue").append(": "), MIN_VALUE,
                MAX_VALUE_RGB, this.blue, SliderType.BLUE
            );
        } else {
            this.blueSlider.setX(x);
            this.blueSlider.setY(y);
        }

        if (brightnessSlider == null) {
            this.brightnessSlider = new ColorSlider(
                x, y, SLIDER_WIDTH, WIDGET_HEIGHT, Component.translatable("gui.rgbblocks.brightness").append(": "),
                MIN_VALUE, MAX_VALUE_SB, this.brightness, SliderType.BRIGHTNESS
            );
        } else {
            this.brightnessSlider.setX(x);
            this.brightnessSlider.setY(y);
        }

        y += SPACING;

        x = this.width / 2 - BOX_WIDTH / 2;

        this.hexBox = new EditBox(this.font, x, y, BOX_WIDTH, WIDGET_HEIGHT, Component.literal("Hex")) {
            UnaryOperator<String> formatter = string -> {
                if (string.contains("#")) {
                    String substringed = string.substring(1);
                    if (substringed.length() < 6) {
                        int len = substringed.length();
                        for (int i = 0; i < 6 - len; i++) {
                            substringed = substringed.concat("0");
                        }
                    }
                    return substringed.isEmpty() ? "000000" : substringed;
                } else if (string.isEmpty()) {
                    return "000000";
                } else {
                    return string;
                }
            };

            @Override
            public void insertText(String textToWrite) {
                textToWrite = textToWrite.contains("#") ? textToWrite.substring(1) : textToWrite;
                textToWrite = textToWrite.toUpperCase(Locale.ENGLISH);
                super.insertText(textToWrite);
                try {
                    Color color = new Color(Integer.parseInt(formatter.apply(getValue()), 16));
                    redSlider.setValueInt(color.getRed());
                    greenSlider.setValueInt(color.getGreen());
                    blueSlider.setValueInt(color.getBlue());
                } catch (NumberFormatException e) {
                    RGBBlocks.LOGGER.debug(textToWrite);
                }
                int cursorPosition = this.getCursorPosition();
                this.setValue("#" + (getValue().contains("#") ? getValue().substring(1) : getValue()));
                if (this.getCursorPosition() != cursorPosition) {
                    this.setHighlightPos(cursorPosition);
                }
                this.setCursorPosition(cursorPosition);
            }

            @Override
            public void deleteChars(int pNum) {
                super.deleteChars(pNum);
                try {
                    Color color = new Color(Integer.parseInt(formatter.apply(getValue()), 16));
                    redSlider.setValueInt(color.getRed());
                    greenSlider.setValueInt(color.getGreen());
                    blueSlider.setValueInt(color.getBlue());
                } catch (NumberFormatException e) {
                    RGBBlocks.LOGGER.debug(getValue());
                }
                int cursorPosition = this.getCursorPosition();
                this.setValue("#" + (getValue().contains("#") ? getValue().substring(1) : getValue()));
                if (this.getCursorPosition() != cursorPosition) {
                    this.setHighlightPos(cursorPosition);
                }
                this.setCursorPosition(cursorPosition);
            }
        };

        this.hexBox.setMaxLength(7);
        this.hexBox.setFilter((string) -> string.matches("(#|)([0-9A-F]){0,6}"));
        this.hexBox.setValue(
            "#" + Integer
                .toHexString(
                    new Color(redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt()).getRGB()
                )
                .substring(2)
                .toUpperCase(Locale.ENGLISH)
        );

        y += SPACING;

        x = this.width / 2 - BUTTON_WIDTH / 2;

        Button toggleButton = new Button.Builder(isRGBSelected ? useHSBText : useRGBText, button -> {
            redSlider.visible = !redSlider.visible;
            greenSlider.visible = !greenSlider.visible;
            blueSlider.visible = !blueSlider.visible;

            hueSlider.visible = !hueSlider.visible;
            saturationSlider.visible = !saturationSlider.visible;
            brightnessSlider.visible = !brightnessSlider.visible;

            hexBox.visible = !hexBox.visible;

            isRGBSelected = !isRGBSelected;

            if (isRGBSelected) {
                Color color = Color.getHSBColor(
                    (float) (hueSlider.getValueInt() / MAX_VALUE_HUE),
                    (float) (saturationSlider.getValueInt() / MAX_VALUE_SB),
                    (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB)
                );

                redSlider.setValueInt(color.getRed());
                greenSlider.setValueInt(color.getGreen());
                blueSlider.setValueInt(color.getBlue());

                hexBox.setValue("#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase(Locale.ENGLISH));

                button.setY(button.getY() + SPACING);

                button.setMessage(useHSBText);
            } else {
                float[] hsb =
                    Color.RGBtoHSB(redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt());

                hueSlider.setValueInt((int) (hsb[0] * MAX_VALUE_HUE));
                saturationSlider.setValueInt((int) (hsb[1] * MAX_VALUE_SB));
                brightnessSlider.setValueInt((int) (hsb[2] * MAX_VALUE_SB));

                button.setY(button.getY() - SPACING);

                button.setMessage(useRGBText);
            }
        }).bounds(x, y, BUTTON_WIDTH, WIDGET_HEIGHT).build();

        if (isRGBSelected) {
            hueSlider.visible = false;
            saturationSlider.visible = false;
            brightnessSlider.visible = false;
        } else {
            redSlider.visible = false;
            greenSlider.visible = false;
            blueSlider.visible = false;
            hexBox.visible = false;
            toggleButton.setY(toggleButton.getY() - SPACING);
        }

        addRenderableWidget(redSlider);
        addRenderableWidget(greenSlider);
        addRenderableWidget(blueSlider);

        addRenderableWidget(hueSlider);
        addRenderableWidget(saturationSlider);
        addRenderableWidget(brightnessSlider);

        addRenderableWidget(hexBox);

        addRenderableWidget(toggleButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.width / 2, this.height / 2 - WIDGET_HEIGHT / 2 - 2 * SPACING - 15, 0);
        guiGraphics.fill(-SLIDER_WIDTH / 2, -WIDGET_HEIGHT, SLIDER_WIDTH / 2, WIDGET_HEIGHT, 0xFF000000);
        guiGraphics
            .fill(-SLIDER_WIDTH / 2 + 1, -WIDGET_HEIGHT + 1, SLIDER_WIDTH / 2 - 1, WIDGET_HEIGHT - 1, getColor());
        guiGraphics.pose().popPose();
    }

    @Override
    public void onClose() {
        PacketHandler.sendToServer(new PaintBucketSyncPKT(getColor(), isRGBSelected));
        super.onClose();
    }
}
