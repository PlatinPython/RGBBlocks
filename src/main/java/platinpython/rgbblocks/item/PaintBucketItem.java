package platinpython.rgbblocks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.util.ClientUtils;
import platinpython.rgbblocks.util.Util;
import platinpython.rgbblocks.util.compat.framedblocks.RGBBlocksFramedBlocks;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

import java.util.function.Consumer;

public class PaintBucketItem extends Item {
    public PaintBucketItem(Properties properties) {
        super(properties);
    }

    // @Override
    // public void verifyComponentsAfterLoad(ItemStack stack) {
    // super.verifyComponentsAfterLoad(stack);
    // if (stack.has(DataComponents.CUSTOM_DATA)) {
    // stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(tag -> {
    // if (tag.contains("color")) {
    // stack.set(DataComponentRegistry.COLOR, tag.getInt("color"));
    // tag.remove("color");
    // }
    // if (tag.contains("isRGBSelected")) {
    // stack.set(DataComponentRegistry.RGB_SELECTED, tag.getBoolean("isRGBSelected"));
    // tag.remove("isRGBSelected");
    // }
    // }));
    // }
    // }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
        ItemStack stack,
        TooltipContext context,
        TooltipDisplay display,
        Consumer<Component> builder,
        TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
        Util.appendHoverText(stack, builder, tooltipFlag);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return super.getMaxDamage(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                ClientUtils.openColorSelectScreen(
                    player.getMainHandItem().getOrDefault(DataComponentRegistry.COLOR, -1),
                    player.getMainHandItem().getOrDefault(DataComponentRegistry.RGB_SELECTED, true)
                );
                return InteractionResult.SUCCESS.heldItemTransformedTo(player.getMainHandItem());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                context.getItemInHand().set(DataComponentRegistry.COLOR, rgbBlockEntity.getColor());
            } else {
                int color = context.getItemInHand().getOrDefault(DataComponentRegistry.COLOR, -1);
                if (!context.getPlayer().isCreative() && color != rgbBlockEntity.getColor()) {
                    if (context.getItemInHand().getDamageValue() == context.getItemInHand().getMaxDamage() - 1) {
                        context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                    } else {
                        context.getItemInHand().hurtAndBreak(1, context.getPlayer(), context.getHand());
                    }
                }
                rgbBlockEntity.setColor(color);
                context.getLevel()
                    .sendBlockUpdated(
                        context.getClickedPos(), blockEntity.getBlockState(), blockEntity.getBlockState(),
                        Block.UPDATE_ALL_IMMEDIATE
                    );
            }
            return InteractionResult.SUCCESS;
        }
        if (ModList.get().isLoaded("framedblocks")) {
            return RGBBlocksFramedBlocks.handlePaintBucketInteraction(context);
        }
        return InteractionResult.PASS;
    }
}
