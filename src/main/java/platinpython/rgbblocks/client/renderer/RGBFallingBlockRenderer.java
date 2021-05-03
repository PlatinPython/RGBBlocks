package platinpython.rgbblocks.client.renderer;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;

public class RGBFallingBlockRenderer extends EntityRenderer<RGBFallingBlockEntity> {
	public RGBFallingBlockRenderer(EntityRendererManager p_i46177_1_) {
		super(p_i46177_1_);
		this.shadowRadius = 0.5f;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(RGBFallingBlockEntity fallingBlockEntity, float entityYaw, float partialTicks,
			MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, int packedLightIn) {
		BlockState blockstate = fallingBlockEntity.getBlockState();
		if (blockstate.getRenderShape() == BlockRenderType.MODEL) {
			World world = fallingBlockEntity.getLevel();
			if (blockstate != world.getBlockState(fallingBlockEntity.blockPosition())
					&& blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
				matrixStackIn.pushPose();
				BlockPos blockpos = new BlockPos(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY,
						fallingBlockEntity.getZ());
				matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
				BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
				for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType
						.chunkBufferLayers()) {
					if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
						net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
						blockrendererdispatcher.getModelRenderer().tesselateBlock(world,
								blockrendererdispatcher.getBlockModel(blockstate), blockstate, blockpos, matrixStackIn,
								renderTypeBuffer.getBuffer(type), false, new Random(),
								blockstate.getSeed(fallingBlockEntity.getStartPos()), OverlayTexture.NO_OVERLAY);
					}
				}
				net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
				matrixStackIn.popPose();
				super.render(fallingBlockEntity, entityYaw, partialTicks, matrixStackIn, renderTypeBuffer, packedLightIn);
			}
		}
	}

	@Override
	public ResourceLocation getTextureLocation(RGBFallingBlockEntity fallingBlockEntity) {
		return new ResourceLocation(RGBBlocks.MOD_ID, "concrete_powder");
	}
}
