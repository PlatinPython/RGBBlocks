package platinpython.rgbblocks.client.renderer.entity;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;

public class RGBFallingBlockRenderer extends EntityRenderer<RGBFallingBlockEntity> {
//	private final ItemRenderer itemRenderer;

	public RGBFallingBlockRenderer(Context context) {
		super(context);
		this.shadowRadius = 0.5f;
//		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(RGBFallingBlockEntity fallingBlockEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
//		matrixStackIn.pushPose();
//		matrixStackIn.translate(0.0D, 0.5D, 0.0D);
//		ItemStack stack = new ItemStack(BlockRegistry.RGB_CONCRETE_POWDER.get());
//		stack.getOrCreateTag().putInt("color", fallingBlockEntity.getColor());
//		itemRenderer.renderStatic(stack, TransformType.NONE, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, renderTypeBuffer, fallingBlockEntity.getId());
//		matrixStackIn.popPose();
		RGBBlocks.LOGGER.info(fallingBlockEntity.getBlockState());
		BlockState blockstate = fallingBlockEntity.getBlockState();
		if (blockstate.getRenderShape() == RenderShape.MODEL) {
			Level level = fallingBlockEntity.getLevel();
			if (blockstate != level.getBlockState(fallingBlockEntity.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
				poseStack.pushPose();
				BlockPos blockpos = new BlockPos(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.getZ());
				poseStack.translate(-0.5D, 0.0D, -0.5D);
				BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();
				for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
					if (ItemBlockRenderTypes.canRenderInLayer(blockstate, type)) {
						net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
						blockrenderdispatcher.getModelRenderer().tesselateBlock(level, blockrenderdispatcher.getBlockModel(blockstate), blockstate, blockpos, poseStack, buffer.getBuffer(type), false, new Random(), blockstate.getSeed(fallingBlockEntity.getStartPos()), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
					}
				}
				net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
				poseStack.popPose();
				super.render(fallingBlockEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
			}
		}
	}

	@Override
	public ResourceLocation getTextureLocation(RGBFallingBlockEntity fallingBlockEntity) {
		return new ResourceLocation(RGBBlocks.MOD_ID, "concrete_powder");
	}
}
