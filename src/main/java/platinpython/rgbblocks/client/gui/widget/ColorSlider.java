package platinpython.rgbblocks.client.gui.widget;

import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import platinpython.rgbblocks.client.gui.ScreenUtils;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.Color;

public class ColorSlider extends AbstractSlider {
	private SliderType type;

	private double minValue;
	private double maxValue;

	private ITextComponent displayText;

	public ColorSlider(int x, int y, int width, int height, ITextComponent displayText, double minValue, double maxValue, double currentValue, SliderType type) {
		super(x, y, width, height, displayText, (currentValue - minValue) / (maxValue - minValue));
		this.type = type;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.value = (currentValue - this.minValue) / (this.maxValue - this.minValue);
		this.displayText = displayText;
		setMessage(new StringTextComponent("").append(displayText).append(Integer.toString((int) Math.round(this.value * (maxValue - minValue) + minValue))));
	}

	@Override
	protected void updateMessage() {
		setMessage(new StringTextComponent("").append(this.displayText).append(Integer.toString(getValueInt())));
	}

	@Override
	protected void applyValue() {
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
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			Minecraft minecraft = Minecraft.getInstance();
			fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, 0xFF000000);
			if (minecraft.screen instanceof ColorSelectScreen) {
				ColorSelectScreen screen = (ColorSelectScreen) minecraft.screen;
				switch (type) {
				case RED:
					renderRedBackground(matrixStack, screen);
					break;
				case GREEN:
					renderGreenBackground(matrixStack, screen);
					break;
				case BLUE:
					renderBlueBackground(matrixStack, screen);
					break;
				case HUE:
					renderHueBackground(matrixStack, screen);
					break;
				case SATURATION:
					renderSaturationBackground(matrixStack, screen);
					break;
				case BRIGHTNESS:
					renderBrightnessBackground(matrixStack, screen);
					break;
				}
			}

			this.renderBg(matrixStack, minecraft, mouseX, mouseY);

			drawCenteredString(matrixStack, minecraft.font, this.getMessage(), this.x + this.width / 2, this.y - (this.height) / 10 * 7, getFGColor());
		}
	}

	private void renderRedBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		int leftColor = new Color(0x00, screen.greenSlider.getValueInt(), screen.blueSlider.getValueInt()).getRGB();
		int rightColor = new Color(0xFF, screen.greenSlider.getValueInt(), screen.blueSlider.getValueInt()).getRGB();
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), leftColor, rightColor);
	}

	private void renderGreenBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		int leftColor = new Color(screen.redSlider.getValueInt(), 0x00, screen.blueSlider.getValueInt()).getRGB();
		int rightColor = new Color(screen.redSlider.getValueInt(), 0xFF, screen.blueSlider.getValueInt()).getRGB();
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), leftColor, rightColor);
	}

	private void renderBlueBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		int leftColor = new Color(screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(), 0x00).getRGB();
		int rightColor = new Color(screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(), 0xFF).getRGB();
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), leftColor, rightColor);
	}

	private void renderHueBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		Function<Integer, Integer> lerp = (pct) -> (int) Math.floor(MathHelper.lerp(pct / 100f, this.x + 1, this.x + this.width - 1));
		Function<Integer, Integer> color = (pct) -> Color.HSBtoRGB((float) ((pct / 100f)), (float) (screen.saturationSlider.getValueInt() / screen.MAX_VALUE_SB), (float) (screen.brightnessSlider.getValueInt() / screen.MAX_VALUE_SB));
		ScreenUtils.fillGradient(matrixStack, lerp.apply(0), this.y + 1, lerp.apply(17), this.y + this.height - 1, this.getBlitOffset(), color.apply(0), color.apply(17));
		ScreenUtils.fillGradient(matrixStack, lerp.apply(17), this.y + 1, lerp.apply(34), this.y + this.height - 1, this.getBlitOffset(), color.apply(17), color.apply(34));
		ScreenUtils.fillGradient(matrixStack, lerp.apply(34), this.y + 1, lerp.apply(50), this.y + this.height - 1, this.getBlitOffset(), color.apply(34), color.apply(50));
		ScreenUtils.fillGradient(matrixStack, lerp.apply(50), this.y + 1, lerp.apply(66), this.y + this.height - 1, this.getBlitOffset(), color.apply(50), color.apply(66));
		ScreenUtils.fillGradient(matrixStack, lerp.apply(66), this.y + 1, lerp.apply(82), this.y + this.height - 1, this.getBlitOffset(), color.apply(66), color.apply(82));
		ScreenUtils.fillGradient(matrixStack, lerp.apply(82), this.y + 1, lerp.apply(100), this.y + this.height - 1, this.getBlitOffset(), color.apply(82), color.apply(100));
	}

	private void renderSaturationBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		int leftColor = Color.HSBtoRGB((float) (screen.hueSlider.getValue() / screen.MAX_VALUE_HUE), 0.0f, (float) (screen.brightnessSlider.getValue() / screen.MAX_VALUE_SB));
		int rightColor = Color.HSBtoRGB((float) (screen.hueSlider.getValue() / screen.MAX_VALUE_HUE), 1.0f, (float) (screen.brightnessSlider.getValue() / screen.MAX_VALUE_SB));
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), leftColor, rightColor);
	}

	private void renderBrightnessBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		int leftColor = Color.HSBtoRGB((float) (screen.hueSlider.getValue() / screen.MAX_VALUE_HUE), (float) (screen.saturationSlider.getValue() / screen.MAX_VALUE_SB), 0.0f);
		int rightColor = Color.HSBtoRGB((float) (screen.hueSlider.getValue() / screen.MAX_VALUE_HUE), (float) (screen.saturationSlider.getValue() / screen.MAX_VALUE_SB), 1.0f);
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), leftColor, rightColor);
	}
}
