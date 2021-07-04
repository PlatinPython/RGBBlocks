package platinpython.rgbblocks.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class RGBFallingBlockRenderer extends EntityRenderer<RGBFallingBlockEntity> {
	private final ItemRenderer itemRenderer;

	public RGBFallingBlockRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		this.shadowRadius = 0.5f;
		this.itemRenderer = Minecraft.getInstance().getItemRenderer();
	}

	@Override
	public void render(RGBFallingBlockEntity fallingBlockEntity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, int packedLightIn) {
		matrixStackIn.pushPose();
		matrixStackIn.translate(0.0D, 0.5D, 0.0D);
		ItemStack stack = new ItemStack(BlockRegistry.RGB_CONCRETE_POWDER.get());
		stack.getOrCreateTag().putInt("color", fallingBlockEntity.getColor());
		IBakedModel bakedModel = itemRenderer.getModel(stack, null, null);
		itemRenderer.render(stack, TransformType.NONE, true, matrixStackIn, renderTypeBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, bakedModel);
		matrixStackIn.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(RGBFallingBlockEntity fallingBlockEntity) {
		return new ResourceLocation(RGBBlocks.MOD_ID, "concrete_powder");
	}
}
