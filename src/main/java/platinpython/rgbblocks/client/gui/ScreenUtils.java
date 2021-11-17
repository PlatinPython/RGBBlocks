package platinpython.rgbblocks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

public class ScreenUtils {
    @SuppressWarnings("deprecation")
    public static void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int z, int colorFrom,
                                    int colorTo) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        fillGradient(matrixStack.last().pose(), bufferbuilder, x1, y1, x2, y2, z, colorFrom, colorTo);
        tessellator.end();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    private static void fillGradient(Matrix4f matrix4f, BufferBuilder builder, int x1, int y1, int x2, int y2, int z,
                                     int colorA, int colorB) {
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
