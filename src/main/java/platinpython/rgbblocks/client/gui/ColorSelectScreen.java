package platinpython.rgbblocks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import platinpython.rgbblocks.client.gui.widget.ColorSlider;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.network.packets.PaintBucketSyncPKT;

public class ColorSelectScreen extends Screen {
	private double red, green, blue;
	public ColorSlider redSlider, greenSlider, blueSlider;
	private double hue, saturation, brightness;
	public ColorSlider hueSlider, saturationSlider, brightnessSlider;

	public final int WIDGET_HEIGHT = 20;
	public final int SLIDER_WIDTH = 310;
	public final int BUTTON_WIDTH = 50;
	public final int SPACING = WIDGET_HEIGHT + 20;

	public final double MIN_VALUE = 0.0D;
	public final double MAX_VALUE_RGB = 255.0D;
	public final double MAX_VALUE_HUE = 360.0D;
	public final double MAX_VALUE_SB = 100.0D;

	private boolean isRGBSelected;

	private final StringTextComponent EMPTY_TEXT = new StringTextComponent("");
	private TranslationTextComponent useRGBText, useHSBText;

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
		this.redSlider = new ColorSlider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 - SPACING, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.red").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_RGB, this.red, false, true, null, SliderType.RED);

		this.greenSlider = new ColorSlider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.green").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_RGB, this.green, false, true, null, SliderType.GREEN);

		this.blueSlider = new ColorSlider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 + SPACING, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.blue").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_RGB, this.blue, false, true, null, SliderType.BLUE);

		this.hueSlider = new ColorSlider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 - SPACING, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.hue").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_HUE, this.hue, false, true, null, SliderType.HUE);

		this.saturationSlider = new ColorSlider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.saturation").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_SB, this.saturation, false, true, null, SliderType.SATURATION);

		this.brightnessSlider = new ColorSlider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 + SPACING, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.brightness").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_SB, this.brightness, false, true, null, SliderType.BRIGHTNESS);

//		TextFieldWidget hex = new TextFieldWidget(font, this.width / 2 - SLIDER_WIDTH / 2,
//				this.height / 2 - sliderHeight / 2 + 2 * (sliderHeight + 20), SLIDER_WIDTH / 4,
//				sliderHeight, new StringTextComponent("Hex"));

		ExtendedButton toggleButton = new ExtendedButton(this.width / 2 - BUTTON_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 + 2 * SPACING, BUTTON_WIDTH, WIDGET_HEIGHT, isRGBSelected ? useHSBText : useRGBText, button -> {
			redSlider.visible = !redSlider.visible;
			greenSlider.visible = !greenSlider.visible;
			blueSlider.visible = !blueSlider.visible;

			hueSlider.visible = !hueSlider.visible;
			saturationSlider.visible = !saturationSlider.visible;
			brightnessSlider.visible = !brightnessSlider.visible;

			isRGBSelected = !isRGBSelected;

			if (isRGBSelected) {
				Color color = Color.getHSBColor((float) (hueSlider.getValueInt() / MAX_VALUE_HUE), (float) (saturationSlider.getValueInt() / MAX_VALUE_SB), (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB));

				redSlider.setValue(color.getRed());
				greenSlider.setValue(color.getGreen());
				blueSlider.setValue(color.getBlue());

				redSlider.updateSlider();
				greenSlider.updateSlider();
				blueSlider.updateSlider();

				button.setMessage(useHSBText);
			} else {
				float[] hsb = Color.RGBtoHSB(redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt());

				hueSlider.setValue(hsb[0] * MAX_VALUE_HUE);
				saturationSlider.setValue(hsb[1] * MAX_VALUE_SB);
				brightnessSlider.setValue(hsb[2] * MAX_VALUE_SB);

				hueSlider.updateSlider();
				saturationSlider.updateSlider();
				brightnessSlider.updateSlider();

				button.setMessage(useRGBText);
			}
		});

		if (isRGBSelected)

		{
			hueSlider.visible = false;
			saturationSlider.visible = false;
			brightnessSlider.visible = false;
		} else {
			redSlider.visible = false;
			greenSlider.visible = false;
			blueSlider.visible = false;
		}

		addWidget(redSlider);
		addWidget(greenSlider);
		addWidget(blueSlider);

		addWidget(hueSlider);
		addWidget(saturationSlider);
		addWidget(brightnessSlider);

//		addButton(hex);

		addButton(toggleButton);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		for (int i = 0; i < this.children.size(); ++i) {
			((Widget) this.children.get(i)).render(matrixStack, mouseX, mouseY, partialTicks);
		}
		matrixStack.pushPose();
		matrixStack.translate(this.width / 2, this.height / 4, 0);
		fill(matrixStack, -10, -10, 10, 10, getColor());
		matrixStack.popPose();
	}

	@Override
	public void onClose() {
		PacketHandler.sendToServer(new PaintBucketSyncPKT(getColor(), isRGBSelected));
		super.onClose();
	}
}
