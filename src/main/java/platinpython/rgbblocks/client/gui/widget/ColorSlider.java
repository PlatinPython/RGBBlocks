package platinpython.rgbblocks.client.gui.widget;

import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import platinpython.rgbblocks.client.gui.ColorSelectScreen;
import platinpython.rgbblocks.client.gui.ScreenUtils;
import platinpython.rgbblocks.client.gui.SliderType;
import platinpython.rgbblocks.util.Color;

public class ColorSlider extends Slider {
	private SliderType type;

	public ColorSlider(int xPos, int yPos, int width, int height, ITextComponent prefix, ITextComponent suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, IPressable handler, SliderType type) {
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, handler);
		this.type = type;
	}

	public void playDownSound(SoundHandler pHandler) {
	}

	public void onRelease(double pMouseX, double pMouseY) {
		super.playDownSound(Minecraft.getInstance().getSoundManager());
		this.dragging = false;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			Minecraft mc = Minecraft.getInstance();
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, 0xFF000000);
			if (mc.screen instanceof ColorSelectScreen) {
				ColorSelectScreen screen = (ColorSelectScreen) mc.screen;
				switch (type) {
				case RED:
					renderRedBackground(matrixStack, screen);
					setFGColor(mouseY);
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

			this.renderBg(matrixStack, mc, mouseX, mouseY);

			ITextComponent buttonText = this.getMessage();
			int strWidth = mc.font.width(buttonText);
			int ellipsisWidth = mc.font.width("...");

			if (strWidth > this.width - 6 && strWidth > ellipsisWidth)
				buttonText = new StringTextComponent(mc.font.substrByWidth(buttonText, this.width - 6 - ellipsisWidth).getString() + "...");

			drawCenteredString(matrixStack, mc.font, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, getFGColor());
		}
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {
		if (this.visible) {
			if (this.dragging) {
				this.sliderValue = (mouseX - (this.x + 4)) / (float) (this.width - 8);
				updateSlider();
			}

			minecraft.getTextureManager().bind(WIDGETS_LOCATION);
			int i = (this.isHovered() ? 2 : 1) * 20;
			this.blit(matrixStack, this.x + (int) (this.sliderValue * (double) (this.width - 8)), this.y, 0, 46 + i, 4, 20);
			this.blit(matrixStack, this.x + (int) (this.sliderValue * (double) (this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
		}
	}

	private void renderRedBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), new Color(0x00, screen.greenSlider.getValueInt(), screen.blueSlider.getValueInt()).getRGB(), new Color(0xFF, screen.greenSlider.getValueInt(), screen.blueSlider.getValueInt()).getRGB());
	}

	private void renderGreenBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), new Color(screen.redSlider.getValueInt(), 0, screen.blueSlider.getValueInt()).getRGB(), new Color(screen.redSlider.getValueInt(), 0xFF, screen.blueSlider.getValueInt()).getRGB());
	}

	private void renderBlueBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), new Color(screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(), 0).getRGB(), new Color(screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(), 0xFF).getRGB());
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
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), Color.HSBtoRGB((float) (screen.hueSlider.getValue() / screen.MAX_VALUE_HUE), 0.0f, (float) (screen.brightnessSlider.getValue() / screen.MAX_VALUE_SB)), Color.HSBtoRGB((float) (screen.hueSlider.getValue() / screen.MAX_VALUE_HUE), 1.0f, (float) (screen.brightnessSlider.getValue() / screen.MAX_VALUE_SB)));
	}

	private void renderBrightnessBackground(MatrixStack matrixStack, ColorSelectScreen screen) {
		ScreenUtils.fillGradient(matrixStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.getBlitOffset(), Color.HSBtoRGB((float) (screen.hueSlider.getValue() / screen.MAX_VALUE_HUE), (float) (screen.saturationSlider.getValue() / screen.MAX_VALUE_SB), 0.0f), Color.HSBtoRGB((float) (screen.hueSlider.getValue() / screen.MAX_VALUE_HUE), (float) (screen.saturationSlider.getValue() / screen.MAX_VALUE_SB), 1.0f));
	}
}
