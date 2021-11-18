package platinpython.rgbblocks.item;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Constants.BlockFlags;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.ClientUtils;
import platinpython.rgbblocks.util.Color;

public class PaintBucketItem extends Item {
    public PaintBucketItem() {
        super(new Properties().tab(RGBBlocks.ITEM_GROUP_RGB).defaultDurability(500).setNoRepair());
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            items.add(getDefaultInstance());
        }
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
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Color color = new Color(stack.getOrCreateTag().getInt("color"));
        if (ClientUtils.hasShiftDown()) {
            MutableComponent red = new TranslatableComponent("gui.rgbblocks.red").append(": " + color.getRed());
            MutableComponent green = new TranslatableComponent("gui.rgbblocks.green").append(": " + color.getGreen());
            MutableComponent blue = new TranslatableComponent("gui.rgbblocks.blue").append(": " + color.getBlue());
            tooltip.add(red.append(", ").append(green).append(", ").append(blue));
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
            MutableComponent hue = new TranslatableComponent("gui.rgbblocks.hue").append(": " +
                                                                                         Math.round(hsb[0] *
                                                                                                    ColorSelectScreen.MAX_VALUE_HUE));
            MutableComponent saturation = new TranslatableComponent("gui.rgbblocks.saturation").append(": " +
                                                                                                       Math.round(hsb[1] *
                                                                                                                  ColorSelectScreen.MAX_VALUE_SB));
            MutableComponent brightness = new TranslatableComponent("gui.rgbblocks.brightness").append(": " +
                                                                                                       Math.round(hsb[2] *
                                                                                                                  ColorSelectScreen.MAX_VALUE_SB));
            tooltip.add(hue.append("°, ").append(saturation).append("%, ").append(brightness).append("%"));
        } else {
            tooltip.add(new TextComponent("#" + Integer.toHexString(color.getRGB()).substring(2)));
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
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (handIn == InteractionHand.MAIN_HAND && playerIn.isShiftKeyDown()) {
            if (worldIn.isClientSide) {
                ClientUtils.openColorSelectScreen(playerIn.getMainHandItem().getTag().getInt("color"),
                                                  playerIn.getMainHandItem().getTag().getBoolean("isRGBSelected"));
                return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, playerIn.getMainHandItem());
            }
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (tileEntity instanceof RGBTileEntity) {
            if (context.getPlayer().isShiftKeyDown()) {
                context.getItemInHand().getTag().putInt("color", ((RGBTileEntity) tileEntity).getColor());
            } else {
                if (context.getItemInHand().getOrCreateTag().getInt("color") !=
                    ((RGBTileEntity) tileEntity).getColor()) {
                    if (context.getItemInHand().getDamageValue() == context.getItemInHand().getMaxDamage() - 1) {
                        context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                    } else {
                        context.getItemInHand()
                               .hurtAndBreak(1, context.getPlayer(), e -> e.broadcastBreakEvent(context.getHand()));
                    }
                }
                ((RGBTileEntity) tileEntity).setColor(context.getItemInHand().getTag().getInt("color"));
                context.getLevel()
                       .sendBlockUpdated(context.getClickedPos(),
                                         tileEntity.getBlockState(),
                                         tileEntity.getBlockState(),
                                         BlockFlags.DEFAULT_AND_RERENDER);
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }
}
