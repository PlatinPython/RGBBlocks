package platinpython.rgbblocks.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.client.renderer.entity.state.RGBFallingBlockRenderState;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;

public class RGBFallingBlockRenderer extends EntityRenderer<RGBFallingBlockEntity, RGBFallingBlockRenderState> {
    public RGBFallingBlockRenderer(Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }
    // private final ItemRenderer itemRenderer;

    // public RGBFallingBlockRenderer(Context context) {
    // super(context);
    // this.shadowRadius = 0.5f;
    // this.itemRenderer = context.getItemRenderer();
    // }

    public boolean shouldRender(RGBFallingBlockEntity entity, Frustum culler, double camX, double camY, double camZ) {
        return super.shouldRender(entity, culler, camX, camY, camZ)
            && entity.getBlockState() != entity.level().getBlockState(entity.blockPosition());
    }

    @Override
    public void submit(
        RGBFallingBlockRenderState state,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        CameraRenderState camera
    ) {
        BlockState blockState = state.movingBlockRenderState.blockState;
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            poseStack.pushPose();
            poseStack.translate(-0.5, 0.0, -0.5);
            submitNodeCollector.submitMovingBlock(poseStack, state.movingBlockRenderState);
            poseStack.popPose();
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

    @Override
    public RGBFallingBlockRenderState createRenderState() {
        return new RGBFallingBlockRenderState();
    }

    @Override
    public void extractRenderState(RGBFallingBlockEntity entity, RGBFallingBlockRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        state.movingBlockRenderState.randomSeedPos = entity.getStartPos();
        state.movingBlockRenderState.blockPos = pos;
        state.movingBlockRenderState.blockState = entity.getBlockState();
        if (entity.level() instanceof ClientLevel clientLevel) {
            state.movingBlockRenderState.biome = clientLevel.getBiome(pos);
            state.movingBlockRenderState.cardinalLighting = clientLevel.cardinalLighting();
            state.movingBlockRenderState.lightEngine = clientLevel.getLightEngine();
        }
        state.color = entity.getColor();
    }

    // @Override
    // public void render(
    // RGBFallingBlockEntity fallingBlockEntity,
    // float entityYaw,
    // float partialTicks,
    // PoseStack poseStack,
    // MultiBufferSource buffer,
    // int packedLight
    // ) {
    // poseStack.pushPose();
    // poseStack.translate(0.0D, 0.5D, 0.0D);
    // ItemStack stack = new ItemStack(fallingBlockEntity.getBlockState().getBlock());
    // stack.set(DataComponentRegistry.COLOR, fallingBlockEntity.getColor());
    // itemRenderer.renderStatic(
    // stack, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, null,
    // fallingBlockEntity.getId()
    // );
    // poseStack.popPose();
    // }
    //
    // @Override
    // public ResourceLocation getTextureLocation(RGBFallingBlockEntity fallingBlockEntity) {
    // return ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "concrete_powder");
    // }
}
