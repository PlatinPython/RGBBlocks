package platinpython.rgbblocks.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.ClientUtils;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.compat.framedblocks.RGBBlocksFramedBlocks;

import java.util.List;

public class PaintBucketItem extends Item {
    public PaintBucketItem() {
        super(new Properties().defaultDurability(500).setNoRepair());
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        CompoundTag compound = stack.getOrCreateTag();
        compound.putInt("color", -1);
        compound.putBoolean("isRGBSelected", true);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        Color color = new Color(stack.getOrCreateTag().getInt("color"));
        if (ClientUtils.hasShiftDown()) {
            MutableComponent red = Component.translatable("gui.rgbblocks.red").append(": " + color.getRed());
            MutableComponent green = Component.translatable("gui.rgbblocks.green").append(": " + color.getGreen());
            MutableComponent blue = Component.translatable("gui.rgbblocks.blue").append(": " + color.getBlue());
            tooltip.add(red.append(", ").append(green).append(", ").append(blue));
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
            MutableComponent hue = Component.translatable("gui.rgbblocks.hue")
                .append(": " + Math.round(hsb[0] * ColorSelectScreen.MAX_VALUE_HUE));
            MutableComponent saturation = Component.translatable("gui.rgbblocks.saturation")
                .append(": " + Math.round(hsb[1] * ColorSelectScreen.MAX_VALUE_SB));
            MutableComponent brightness = Component.translatable("gui.rgbblocks.brightness")
                .append(": " + Math.round(hsb[2] * ColorSelectScreen.MAX_VALUE_SB));
            tooltip.add(hue.append("°, ").append(saturation).append("%, ").append(brightness).append("%"));
        } else {
            tooltip.add(Component.literal("#" + Integer.toHexString(color.getRGB()).substring(2)));
        }
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
    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        if (handIn == InteractionHand.MAIN_HAND && playerIn.isShiftKeyDown()
            && playerIn.getMainHandItem().getTag() != null) {
            if (level.isClientSide) {
                ClientUtils.openColorSelectScreen(
                    playerIn.getMainHandItem().getTag().getInt("color"),
                    playerIn.getMainHandItem().getTag().getBoolean("isRGBSelected")
                );
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getMainHandItem());
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                context.getItemInHand().getOrCreateTag().putInt("color", rgbBlockEntity.getColor());
            } else {
                if (!context.getItemInHand().hasTag()) {
                    return InteractionResult.PASS;
                }
                // noinspection DataFlowIssue
                int color = context.getItemInHand().getTag().getInt("color");
                if (!context.getPlayer().isCreative() && color != rgbBlockEntity.getColor()) {
                    if (context.getItemInHand().getDamageValue() == context.getItemInHand().getMaxDamage() - 1) {
                        context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                    } else {
                        context.getItemInHand()
                            .hurtAndBreak(1, context.getPlayer(), e -> e.broadcastBreakEvent(context.getHand()));
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

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }
}
