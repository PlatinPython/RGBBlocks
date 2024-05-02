package platinpython.rgbblocks.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.ClientUtils;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

import java.util.List;

public class RGBBlockItem extends BlockItem {
    public RGBBlockItem(Block blockIn) {
        super(blockIn, new Item.Properties().component(DataComponentRegistry.COLOR, -1));
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(tag -> {
                if (tag.contains("color")) {
                    stack.set(DataComponentRegistry.COLOR, tag.getInt("color"));
                    tag.remove("color");
                }
            }));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        Color color = new Color(stack.getOrDefault(DataComponentRegistry.COLOR, -1));
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
}
