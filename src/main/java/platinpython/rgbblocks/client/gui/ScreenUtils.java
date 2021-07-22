package platinpython.rgbblocks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;

public class ScreenUtils {
	public static void fillGradient(PoseStack poseStack, int x1, int y1, int x2, int y2, int z, int colorFrom, int colorTo) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		fillGradient(poseStack.last().pose(), bufferbuilder, x1, y1, x2, y2, z, colorFrom, colorTo);
		tesselator.end();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}

	private static void fillGradient(Matrix4f matrix4f, BufferBuilder builder, int x1, int y1, int x2, int y2, int z, int colorA, int colorB) {
		float alphaA = (colorA >> 24 & 0xFF) / (float) 0xFF;
		float redA = (colorA >> 16 & 0xFF) / (float) 0xFF;
		float greenA = (colorA >> 8 & 0xFF) / (float) 0xFF;
		float blueA = (colorA & 0xFF) / (float) 0xFF;
		float alphaB = (colorB >> 24 & 0xFF) / (float) 0xFF;
		float redB = (colorB >> 16 & 0xFF) / (float) 0xFF;
		float greenB = (colorB >> 8 & 0xFF) / (float) 0xFF;
		float blueB = (colorB & 0xFF) / (float) 0xFF;
		builder.vertex(matrix4f, (float) x2, (float) y1, (float) z).color(redB, greenB, blueB, alphaB).endVertex();
		builder.vertex(matrix4f, (float) x1, (float) y1, (float) z).color(redA, greenA, blueA, alphaA).endVertex();
		builder.vertex(matrix4f, (float) x1, (float) y2, (float) z).color(redA, greenA, blueA, alphaA).endVertex();
		builder.vertex(matrix4f, (float) x2, (float) y2, (float) z).color(redB, greenB, blueB, alphaB).endVertex();
	}
}
