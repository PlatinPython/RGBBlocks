package platinpython.rgbblocks.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.network.packets.PaintbucketSyncPKT;

public class PaintbucketScreen extends Screen {
	private double red, green, blue;
	private Slider redSlider, greenSlider, blueSlider;

	public PaintbucketScreen(int colorIn) {
		super(new StringTextComponent("Paintbucket"));
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
		this.redSlider = new Slider(this.width / 2 - 100, this.height / 6 + 60, 170, 20, new StringTextComponent(""),
				new StringTextComponent(""), 0.0D, 255.0D, this.red, false, false, (button) -> {
				});
		this.greenSlider = new Slider(this.width / 2 - 100, this.height / 6 + 100, 170, 20, new StringTextComponent(""),
				new StringTextComponent(""), 0.0D, 255.0D, this.green, false, false, (button) -> {
				});
		this.blueSlider = new Slider(this.width / 2 - 100, this.height / 6 + 140, 170, 20, new StringTextComponent(""),
				new StringTextComponent(""), 0.0D, 255.0D, this.blue, false, false, (button) -> {
				});
//		TextFieldWidget hex = new TextFieldWidget(this.font, this.width / 2 - 25, this.height / 6 + 180, 50, 20,
//				String.format("#%02X%02X%02X", (int) this.red, (int) this.green, (int) this.blue));
		Button toggleButton = new Button(this.width / 2 - 20, this.height / 6 + 220, 40, 20,
				new StringTextComponent(""), (button) -> {

				});
		addButton(redSlider);
		addButton(greenSlider);
		addButton(blueSlider);
		// addButton(hex);
		addButton(toggleButton);
	}

	@Override
	public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
		drawCenteredString(matrixStack, this.font, getTitle().getString(), this.width / 2, 15, 16777215);
		drawCenteredString(matrixStack, this.font, "Red", this.redSlider.x, this.redSlider.y - 12, 16777215);
		drawCenteredString(matrixStack, this.font, "" + this.red, this.redSlider.x + 180, this.redSlider.y, 16777215);
		drawCenteredString(matrixStack, this.font, "Green", this.greenSlider.x, this.greenSlider.y - 12, 16777215);
		drawCenteredString(matrixStack, this.font, "Blue", this.blueSlider.x, this.blueSlider.y - 12, 16777215);
	}

	@Override
	public void onClose() {
		super.onClose();
		CompoundNBT compound = new CompoundNBT();
		Color color = new Color(this.redSlider.getValueInt(), this.greenSlider.getValueInt(),
				this.blueSlider.getValueInt());
		compound.putInt("color", color.getRGB());
		PacketHandler.sendToServer(new PaintbucketSyncPKT(compound));
	}
}
