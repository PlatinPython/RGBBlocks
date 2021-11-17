package platinpython.rgbblocks.client.gui.screen;

import java.util.Locale;
import java.util.function.UnaryOperator;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.gui.widget.ColorSlider;
import platinpython.rgbblocks.client.gui.widget.SliderType;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.network.packets.PaintBucketSyncPKT;

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

    private final TranslatableComponent useRGBText, useHSBText;

    public ColorSelectScreen(int colorIn, boolean isRGBSelected) {
        super(TextComponent.EMPTY);
        Color color = new Color(colorIn);
        this.red = (double) color.getRed();
        this.green = (double) color.getGreen();
        this.blue = (double) color.getBlue();

        float[] hsb = Color.RGBtoHSB((int) red, (int) green, (int) blue);
        this.hue = hsb[0] * MAX_VALUE_HUE;
        this.saturation = hsb[1] * MAX_VALUE_SB;
        this.brightness = hsb[2] * MAX_VALUE_SB;

        this.isRGBSelected = isRGBSelected;

        this.useRGBText = new TranslatableComponent("gui.rgbblocks.useRGB");
        this.useHSBText = new TranslatableComponent("gui.rgbblocks.useHSB");
    }

    public int getColor() {
        if (isRGBSelected) {
            return new Color(this.redSlider.getValueInt(),
                             this.greenSlider.getValueInt(),
                             this.blueSlider.getValueInt()).getRGB();
        } else {
            return Color.getHSBColor((float) (hueSlider.getValueInt() / MAX_VALUE_HUE),
                                     (float) (saturationSlider.getValueInt() / MAX_VALUE_SB),
                                     (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB)).getRGB();
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
            this.redSlider = new ColorSlider(x,
                                             y,
                                             SLIDER_WIDTH,
                                             WIDGET_HEIGHT,
                                             new TranslatableComponent("gui.rgbblocks.red").append(": "),
                                             MIN_VALUE,
                                             MAX_VALUE_RGB,
                                             this.red,
                                             SliderType.RED);
        } else {
            this.redSlider.x = x;
            this.redSlider.y = y;
        }

        if (hueSlider == null) {
            this.hueSlider = new ColorSlider(x,
                                             y,
                                             SLIDER_WIDTH,
                                             WIDGET_HEIGHT,
                                             new TranslatableComponent("gui.rgbblocks.hue").append(": "),
                                             MIN_VALUE,
                                             MAX_VALUE_HUE,
                                             this.hue,
                                             SliderType.HUE);
        } else {
            this.hueSlider.x = x;
            this.hueSlider.y = y;
        }

        y += SPACING + 15;

        if (greenSlider == null) {
            this.greenSlider = new ColorSlider(x,
                                               y,
                                               SLIDER_WIDTH,
                                               WIDGET_HEIGHT,
                                               new TranslatableComponent("gui.rgbblocks.green").append(": "),
                                               MIN_VALUE,
                                               MAX_VALUE_RGB,
                                               this.green,
                                               SliderType.GREEN);
        } else {
            this.greenSlider.x = x;
            this.greenSlider.y = y;
        }

        if (saturationSlider == null) {
            this.saturationSlider = new ColorSlider(x,
                                                    y,
                                                    SLIDER_WIDTH,
                                                    WIDGET_HEIGHT,
                                                    new TranslatableComponent("gui.rgbblocks.saturation").append(": "),
                                                    MIN_VALUE,
                                                    MAX_VALUE_SB,
                                                    this.saturation,
                                                    SliderType.SATURATION);
        } else {
            this.saturationSlider.x = x;
            this.saturationSlider.y = y;
        }

        y += SPACING + 15;

        if (blueSlider == null) {
            this.blueSlider = new ColorSlider(x,
                                              y,
                                              SLIDER_WIDTH,
                                              WIDGET_HEIGHT,
                                              new TranslatableComponent("gui.rgbblocks.blue").append(": "),
                                              MIN_VALUE,
                                              MAX_VALUE_RGB,
                                              this.blue,
                                              SliderType.BLUE);
        } else {
            this.blueSlider.x = x;
            this.blueSlider.y = y;
        }

        if (brightnessSlider == null) {
            this.brightnessSlider = new ColorSlider(x,
                                                    y,
                                                    SLIDER_WIDTH,
                                                    WIDGET_HEIGHT,
                                                    new TranslatableComponent("gui.rgbblocks.brightness").append(": "),
                                                    MIN_VALUE,
                                                    MAX_VALUE_SB,
                                                    this.brightness,
                                                    SliderType.BRIGHTNESS);
        } else {
            this.brightnessSlider.x = x;
            this.brightnessSlider.y = y;
        }

        y += SPACING;

        x = this.width / 2 - BOX_WIDTH / 2;

        this.hexBox = new EditBox(this.font, x, y, BOX_WIDTH, WIDGET_HEIGHT, new TextComponent("Hex")) {
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
        this.hexBox.setValue("#" +
                             Integer.toHexString(new Color(redSlider.getValueInt(),
                                                           greenSlider.getValueInt(),
                                                           blueSlider.getValueInt()).getRGB())
                                    .substring(2)
                                    .toUpperCase(Locale.ENGLISH));

        y += SPACING;

        x = this.width / 2 - BUTTON_WIDTH / 2;

        Button toggleButton = new Button(x,
                                         y,
                                         BUTTON_WIDTH,
                                         WIDGET_HEIGHT,
                                         isRGBSelected ? useHSBText : useRGBText,
                                         button -> {
                                             redSlider.visible = !redSlider.visible;
                                             greenSlider.visible = !greenSlider.visible;
                                             blueSlider.visible = !blueSlider.visible;

                                             hueSlider.visible = !hueSlider.visible;
                                             saturationSlider.visible = !saturationSlider.visible;
                                             brightnessSlider.visible = !brightnessSlider.visible;

                                             hexBox.visible = !hexBox.visible;

                                             isRGBSelected = !isRGBSelected;

                                             if (isRGBSelected) {
                                                 Color color = Color.getHSBColor((float) (hueSlider.getValueInt() /
                                                                                          MAX_VALUE_HUE),
                                                                                 (float) (saturationSlider.getValueInt() /
                                                                                          MAX_VALUE_SB),
                                                                                 (float) (brightnessSlider.getValueInt() /
                                                                                          MAX_VALUE_SB));

                                                 redSlider.setValueInt(color.getRed());
                                                 greenSlider.setValueInt(color.getGreen());
                                                 blueSlider.setValueInt(color.getBlue());

                                                 hexBox.setValue("#" +
                                                                 Integer.toHexString(color.getRGB())
                                                                        .substring(2)
                                                                        .toUpperCase(Locale.ENGLISH));

                                                 button.y = button.y + SPACING;

                                                 button.setMessage(useHSBText);
                                             } else {
                                                 float[] hsb = Color.RGBtoHSB(redSlider.getValueInt(),
                                                                              greenSlider.getValueInt(),
                                                                              blueSlider.getValueInt());

                                                 hueSlider.setValueInt((int) (hsb[0] * MAX_VALUE_HUE));
                                                 saturationSlider.setValueInt((int) (hsb[1] * MAX_VALUE_SB));
                                                 brightnessSlider.setValueInt((int) (hsb[2] * MAX_VALUE_SB));

                                                 button.y = button.y - SPACING;

                                                 button.setMessage(useRGBText);
                                             }
                                         });

        if (isRGBSelected) {
            hueSlider.visible = false;
            saturationSlider.visible = false;
            brightnessSlider.visible = false;
        } else {
            redSlider.visible = false;
            greenSlider.visible = false;
            blueSlider.visible = false;
            hexBox.visible = false;
            toggleButton.y = toggleButton.y - SPACING;
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        poseStack.pushPose();
        poseStack.translate(this.width / 2, this.height / 2 - WIDGET_HEIGHT / 2 - 2 * SPACING - 15, 0);
        fill(poseStack, -SLIDER_WIDTH / 2, -WIDGET_HEIGHT, SLIDER_WIDTH / 2, WIDGET_HEIGHT, 0xFF000000);
        fill(poseStack, -SLIDER_WIDTH / 2 + 1, -WIDGET_HEIGHT + 1, SLIDER_WIDTH / 2 - 1, WIDGET_HEIGHT - 1, getColor());
        poseStack.popPose();
    }

    @Override
    public void onClose() {
        PacketHandler.sendToServer(new PaintBucketSyncPKT(getColor(), isRGBSelected));
        super.onClose();
    }
}
