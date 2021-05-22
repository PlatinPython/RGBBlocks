package platinpython.rgbblocks.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.network.packets.PaintbucketSyncPKT;

public class PaintbucketScreen extends Screen {
	private double red, green, blue;
	private Slider redSlider, greenSlider, blueSlider;

	private int sliderWidth = 170;
	private int sliderHeight = 20;
	private double minValue = 0.0D;
	private double maxValue = 255.0D;
	private StringTextComponent emptyText = new StringTextComponent("");

	public PaintbucketScreen(int colorIn) {
		super(new TranslationTextComponent("item.rgbblocks.bucket_of_paint"));
		Color color = new Color(colorIn);
		this.red = (double) color.getRed();
		this.green = (double) color.getGreen();
		this.blue = (double) color.getBlue();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		super.init();
		this.redSlider = new Slider(this.width / 2 - sliderWidth / 2,
				this.height / 2 - sliderHeight / 2 - (sliderHeight + 20), sliderWidth, sliderHeight,
				new TranslationTextComponent("gui.rgbblocks.bucket_of_paint.red").append(": "), emptyText, minValue,
				maxValue, this.red, false, true, (button) -> {
				});

		this.greenSlider = new Slider(this.width / 2 - sliderWidth / 2, this.height / 2 - sliderHeight / 2, sliderWidth,
				sliderHeight, new TranslationTextComponent("gui.rgbblocks.bucket_of_paint.green").append(": "),
				emptyText, minValue, maxValue, this.green, false, true, null);

		this.blueSlider = new Slider(this.width / 2 - sliderWidth / 2,
				this.height / 2 - sliderHeight / 2 + (sliderHeight + 20), sliderWidth, sliderHeight,
				new TranslationTextComponent("gui.rgbblocks.bucket_of_paint.blue").append(": "), emptyText, minValue,
				maxValue, this.blue, false, true, null);
		
//		TextFieldWidget hex = new TextFieldWidget(font, this.width / 2 - sliderWidth / 2,
//				this.height / 2 - sliderHeight / 2 + 2 * (sliderHeight + 20), sliderWidth / 4,
//				sliderHeight, new StringTextComponent("Hex"));
		
//		Button toggleButton = new Button(this.width / 2 - 20, this.height / 6 + 220, 40, 20,
//				new StringTextComponent(""), (button) -> {
//
//				});
		addButton(redSlider);
		addButton(greenSlider);
		addButton(blueSlider);
//		addButton(hex);
//		addButton(toggleButton);
	}

	@Override
	public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
		drawCenteredString(matrixStack, this.font, getTitle().getString(), this.width / 2, 15, 16777215);
	}

	@Override
	public void onClose() {
		super.onClose();
		Color color = new Color(this.redSlider.getValueInt(), this.greenSlider.getValueInt(),
				this.blueSlider.getValueInt());
		PacketHandler.sendToServer(new PaintbucketSyncPKT(color.getRGB()));
	}
}
