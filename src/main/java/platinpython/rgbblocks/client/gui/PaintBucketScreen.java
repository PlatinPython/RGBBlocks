package platinpython.rgbblocks.client.gui;

import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.network.packets.PaintBucketSyncPKT;

public class PaintBucketScreen extends Screen {
	private double red, green, blue;
	private Slider redSlider, greenSlider, blueSlider;
	private double hue, saturation, brightness;
	private Slider hueSlider, saturationSlider, brightnessSlider;

	private final int WIDGET_HEIGHT = 20;
	private final int SLIDER_WIDTH = 170;
	private final int BUTTON_WIDTH = 50;
	private final int SPACING = WIDGET_HEIGHT + 20;

	private final double MIN_VALUE = 0.0D;
	private final double MAX_VALUE_RGB = 255.0D;
	private final double MAX_VALUE_HUE = 360.0D;
	private final double MAX_VALUE_SB = 100.0D;

	private boolean isRGBSelected;

	private final StringTextComponent EMPTY_TEXT = new StringTextComponent("");
	private TranslationTextComponent useRGBText, useHSBText;

	public PaintBucketScreen(int colorIn, boolean isRGBSelected) {
		super(new TranslationTextComponent("item.rgbblocks.paint_bucket"));
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

	private int getColor() {
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
		this.redSlider = new Slider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 - SPACING, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.red").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_RGB, this.red, false, true, null);

		this.greenSlider = new Slider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.green").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_RGB, this.green, false, true, null);

		this.blueSlider = new Slider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 + SPACING, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.blue").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_RGB, this.blue, false, true, null);

		this.hueSlider = new Slider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 - SPACING, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.hue").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_HUE, this.hue, false, true, null);

		this.saturationSlider = new Slider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.saturation").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_SB, this.saturation, false, true, null);

		this.brightnessSlider = new Slider(this.width / 2 - SLIDER_WIDTH / 2, this.height / 2 - WIDGET_HEIGHT / 2 + SPACING, SLIDER_WIDTH, WIDGET_HEIGHT, new TranslationTextComponent("gui.rgbblocks.brightness").append(": "), EMPTY_TEXT, MIN_VALUE, MAX_VALUE_SB, this.brightness, false, true, null);

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
		matrixStack.pushPose();
		matrixStack.translate(width / 2, height / 2, 0);
		matrixStack.mulPose(Vector3f.ZN.rotationDegrees(90f));
		if (isRGBSelected) {
			renderRGBPreviews(matrixStack);
		} else {
			renderHSBPreviews(matrixStack);
		}
		matrixStack.popPose();
		for (int i = 0; i < this.children.size(); ++i) {
			((Widget) this.children.get(i)).render(matrixStack, mouseX, mouseY, partialTicks);
		}
		drawCenteredString(matrixStack, this.font, getTitle().getString(), this.width / 2, 15, 16777215);
	}

	private void renderRGBPreviews(MatrixStack matrixStack) {
		int xLeft = -SLIDER_WIDTH / 2;
		int xRight = SLIDER_WIDTH / 2;
		int y = WIDGET_HEIGHT / 2 + WIDGET_HEIGHT;
		int yTop = y + WIDGET_HEIGHT;
		int yBot = y + SPACING;
		fillGradient(matrixStack, yTop, xLeft, yBot, xRight, new Color(0, greenSlider.getValueInt(), blueSlider.getValueInt()).getRGB(), new Color(0xFF, greenSlider.getValueInt(), blueSlider.getValueInt()).getRGB());
		yTop -= SPACING;
		yBot -= SPACING;
		fillGradient(matrixStack, yTop, xLeft, yBot, xRight, new Color(redSlider.getValueInt(), 0, blueSlider.getValueInt()).getRGB(), new Color(redSlider.getValueInt(), 0xFF, blueSlider.getValueInt()).getRGB());
		yTop -= SPACING;
		yBot -= SPACING;
		fillGradient(matrixStack, yTop, xLeft, yBot, xRight, new Color(redSlider.getValueInt(), greenSlider.getValueInt(), 0).getRGB(), new Color(redSlider.getValueInt(), greenSlider.getValueInt(), 0xFF).getRGB());
	}

	private void renderHSBPreviews(MatrixStack matrixStack) {
		Function<Integer, Integer> lerp = (pct) -> (int) Math.floor((pct / 100f) * SLIDER_WIDTH - SLIDER_WIDTH / 2);
		Function<Integer, Integer> color = (pct) -> Color.HSBtoRGB((float) ((pct / 100f)), (float) (saturationSlider.getValueInt() / MAX_VALUE_SB), (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB));
		int y = WIDGET_HEIGHT / 2 + WIDGET_HEIGHT;
		int yTop = y + WIDGET_HEIGHT;
		int yBot = y + SPACING;
		fillGradient(matrixStack, yTop, lerp.apply(0), yBot, lerp.apply(17), color.apply(0), color.apply(17));
		fillGradient(matrixStack, yTop, lerp.apply(17), yBot, lerp.apply(34), color.apply(17), color.apply(34));
		fillGradient(matrixStack, yTop, lerp.apply(34), yBot, lerp.apply(50), color.apply(34), color.apply(50));
		fillGradient(matrixStack, yTop, lerp.apply(50), yBot, lerp.apply(66), color.apply(50), color.apply(66));
		fillGradient(matrixStack, yTop, lerp.apply(66), yBot, lerp.apply(82), color.apply(66), color.apply(82));
		fillGradient(matrixStack, yTop, lerp.apply(82), yBot, lerp.apply(100), color.apply(82), color.apply(100));
		yTop -= SPACING;
		yBot -= SPACING;
		fillGradient(matrixStack, yTop, lerp.apply(0), yBot, lerp.apply(100), Color.HSBtoRGB((float) (hueSlider.getValue() / MAX_VALUE_HUE), 0.0f, (float) (brightnessSlider.getValue() / MAX_VALUE_SB)), Color.HSBtoRGB((float) (hueSlider.getValue() / MAX_VALUE_HUE), 1.0f, (float) (brightnessSlider.getValue() / MAX_VALUE_SB)));
		yTop -= SPACING;
		yBot -= SPACING;
		fillGradient(matrixStack, yTop, lerp.apply(0), yBot, lerp.apply(100), Color.HSBtoRGB((float) (hueSlider.getValue() / MAX_VALUE_HUE), (float) (saturationSlider.getValue() / MAX_VALUE_SB), 0.0f), Color.HSBtoRGB((float) (hueSlider.getValue() / MAX_VALUE_HUE), (float) (saturationSlider.getValue() / MAX_VALUE_SB), 1.0f));
	}

	@Override
	public void onClose() {
		PacketHandler.sendToServer(new PaintBucketSyncPKT(getColor(), isRGBSelected));
		super.onClose();
	}
}
