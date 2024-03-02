package platinpython.rgbblocks.util.compat.framedblocks;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.Color;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.FramedConstants;

public class RGBBlocksFramedBlocks {
    public static final DeferredRegister<CamoContainerFactory> CAMO_CONTAINER_FACTORIES =
        DeferredRegister.create(FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_NAME, RGBBlocks.MOD_ID);

    public static final DeferredHolder<CamoContainerFactory, RGBBlocksCamoContainer.Factory> RGBBLOCKS_CONTAINER_FACTORY =
        CAMO_CONTAINER_FACTORIES.register("container_factory", RGBBlocksCamoContainer.Factory::new);

    public static void register(IEventBus bus) {
        RGBBlocksFramedBlocks.CAMO_CONTAINER_FACTORIES.register(bus);
    }

    public static InteractionResult handlePaintBucketInteraction(UseOnContext context) {
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof FramedBlockEntity framedBlockEntity)) {
            return InteractionResult.PASS;
        }
        BlockHitResult blockHitResult = new BlockHitResult(
            context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()
        );
        if (!(framedBlockEntity.getCamo(blockHitResult) instanceof RGBBlocksCamoContainer camoContainer)) {
            return InteractionResult.PASS;
        }
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            context.getItemInHand().getOrCreateTag().putInt("color", camoContainer.color);
        } else {
            if (!context.getPlayer().getAbilities().instabuild
                && context.getItemInHand().getOrCreateTag().getInt("color") != camoContainer.color) {
                if (context.getItemInHand().getDamageValue() == context.getItemInHand().getMaxDamage() - 1) {
                    context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                } else {
                    context.getItemInHand()
                        .hurtAndBreak(1, context.getPlayer(), e -> e.broadcastBreakEvent(context.getHand()));
                }
            }
            camoContainer.color = context.getItemInHand().getOrCreateTag().getInt("color");
            camoContainer.mapColor = Color.getNearestMapColor(camoContainer.color);
            context.getLevel()
                .sendBlockUpdated(
                    context.getClickedPos(), framedBlockEntity.getBlockState(), framedBlockEntity.getBlockState(),
                    Block.UPDATE_ALL_IMMEDIATE
                );
        }
        return InteractionResult.SUCCESS;
    }
}
