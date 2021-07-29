package platinpython.rgbblocks.client.gui.screen;

import java.util.Locale;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
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
	public TextFieldWidget hexField;

	public final int WIDGET_HEIGHT = 20;
	public final int SLIDER_WIDTH = 310;
	public final int BUTTON_WIDTH = 98;
	public final int FIELD_WIDTH = 50;
	public final int SPACING = WIDGET_HEIGHT + 5;

	public static final double MIN_VALUE = 0.0D;
	public static final double MAX_VALUE_RGB = 255.0D;
	public static final double MAX_VALUE_HUE = 360.0D;
	public static final double MAX_VALUE_SB = 100.0D;

	private boolean isRGBSelected;

	private final TranslationTextComponent useRGBText, useHSBText;

	public ColorSelectScreen(int colorIn, boolean isRGBSelected) {
		super(StringTextComponent.EMPTY);
		Color color = new Color(colorIn);
		this.red = (double) color.getRed();
		this.green = (double) color.getGreen();
		this.blue = (double) color.getBlue();

		float[] hsb = Color.RGBtoHSB((int) red, (int) green, (int) blue);
		this.hue = hsb[0] * MAX_VALUE_HUE;
		this.saturation = hsb[1] * MAX_VALUE_SB;
		this.brightness = hsb[2] * MAX_VALUE_SB;

		this.isRGBSelected = isRGBSelected;

		this.useRGBText = new TranslationTextComponent("gui.rgbblocks.useRGB");
		this.useHSBText = new TranslationTextComponent("gui.rgbblocks.useHSB");
	}

	public int getColor() {
		if (isRGBSelected) {
			return new Color(this.redSlider.getValueInt(), this.greenSlider.getValueInt(), this.blueSlider.getValueInt()).getRGB();
		} else {
			return Color.getHSBColor((float) (hueSlider.getValueInt() / MAX_VALUE_HUE), (float) (saturationSlider.getValueInt() / MAX_VALUE_SB), (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB)).getRGB();
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
			this.redSlider = new ColorSlider(x, y, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.red").append(": "), MIN_VALUE, MAX_VALUE_RGB, this.red, SliderType.RED);
		} else {
			this.redSlider.x = x;
			this.redSlider.y = y;
		}

		if (hueSlider == null) {
			this.hueSlider = new ColorSlider(x, y, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.hue").append(": "), MIN_VALUE, MAX_VALUE_HUE, this.hue, SliderType.HUE);
		} else {
			this.hueSlider.x = x;
			this.hueSlider.y = y;
		}

		y += SPACING + 15;

		if (greenSlider == null) {
			this.greenSlider = new ColorSlider(x, y, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.green").append(": "), MIN_VALUE, MAX_VALUE_RGB, this.green, SliderType.GREEN);
		} else {
			this.greenSlider.x = x;
			this.greenSlider.y = y;
		}

		if (saturationSlider == null) {
			this.saturationSlider = new ColorSlider(x, y, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.saturation").append(": "), MIN_VALUE, MAX_VALUE_SB, this.saturation, SliderType.SATURATION);
		} else {
			this.saturationSlider.x = x;
			this.saturationSlider.y = y;
		}

		y += SPACING + 15;

		if (blueSlider == null) {
			this.blueSlider = new ColorSlider(x, y, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.blue").append(": "), MIN_VALUE, MAX_VALUE_RGB, this.blue, SliderType.BLUE);
		} else {
			this.blueSlider.x = x;
			this.blueSlider.y = y;
		}

		if (brightnessSlider == null) {
			this.brightnessSlider = new ColorSlider(x, y, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.brightness").append(": "), MIN_VALUE, MAX_VALUE_SB, this.brightness, SliderType.BRIGHTNESS);
		} else {
			this.brightnessSlider.x = x;
			this.brightnessSlider.y = y;
		}

		y += SPACING;

		x = this.width / 2 - FIELD_WIDTH / 2;

		this.hexField = new TextFieldWidget(this.font, x, y, FIELD_WIDTH, WIDGET_HEIGHT, new StringTextComponent("Hex")) {
			@Override
			public void insertText(String textToWrite) {
				textToWrite = textToWrite.toUpperCase(Locale.ENGLISH);
				super.insertText(textToWrite);
				this.setValue("#" + getValue());
			}

			@Override
			public void deleteChars(int pNum) {
				super.deleteChars(pNum);
				this.setValue("#" + getValue());
			}
		};

		this.hexField.setMaxLength(7);
		this.hexField.setFilter((string) -> string.matches("(#|)([0-9A-F]){0,6}"));
		this.hexField.setValue("#" + Integer.toHexString(new Color(redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt()).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));

		y += SPACING;

		x = this.width / 2 - BUTTON_WIDTH / 2;

		ExtendedButton toggleButton = new ExtendedButton(x, y, BUTTON_WIDTH, WIDGET_HEIGHT, isRGBSelected ? useHSBText : useRGBText, button -> {
			redSlider.visible = !redSlider.visible;
			greenSlider.visible = !greenSlider.visible;
			blueSlider.visible = !blueSlider.visible;

			hueSlider.visible = !hueSlider.visible;
			saturationSlider.visible = !saturationSlider.visible;
			brightnessSlider.visible = !brightnessSlider.visible;

			isRGBSelected = !isRGBSelected;

			hexField.visible = !hexField.visible;

			if (isRGBSelected) {
				Color color = Color.getHSBColor((float) (hueSlider.getValueInt() / MAX_VALUE_HUE), (float) (saturationSlider.getValueInt() / MAX_VALUE_SB), (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB));

				redSlider.setValueInt(color.getRed());
				greenSlider.setValueInt(color.getGreen());
				blueSlider.setValueInt(color.getBlue());
				
				hexField.setValue("#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase(Locale.ENGLISH));

				button.y = button.y + SPACING;

				button.setMessage(useHSBText);
			} else {
				float[] hsb = Color.RGBtoHSB(redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt());

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
			hexField.visible = false;
			toggleButton.y = toggleButton.y - SPACING;
		}

		addWidget(redSlider);
		addWidget(greenSlider);
		addWidget(blueSlider);

		addWidget(hueSlider);
		addWidget(saturationSlider);
		addWidget(brightnessSlider);

		addButton(hexField);

		addButton(toggleButton);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		for (int i = 0; i < this.children.size(); ++i) {
			((Widget) this.children.get(i)).render(matrixStack, mouseX, mouseY, partialTicks);
		}
		matrixStack.pushPose();
		matrixStack.translate(this.width / 2, this.height / 2 - WIDGET_HEIGHT / 2 - 2 * SPACING - 15, 0);
		fill(matrixStack, -SLIDER_WIDTH / 2, -WIDGET_HEIGHT, SLIDER_WIDTH / 2, WIDGET_HEIGHT, 0xFF000000);
		fill(matrixStack, -SLIDER_WIDTH / 2 + 1, -WIDGET_HEIGHT + 1, SLIDER_WIDTH / 2 - 1, WIDGET_HEIGHT - 1, getColor());
		matrixStack.popPose();
	}

	@Override
	public void onClose() {
		PacketHandler.sendToServer(new PaintBucketSyncPKT(getColor(), isRGBSelected));
		super.onClose();
	}
}
