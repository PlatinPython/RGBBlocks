package platinpython.rgbblocks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import platinpython.rgbblocks.util.Util;

import java.util.function.Consumer;

public class RGBBlockItem extends BlockItem {
    public RGBBlockItem(Block blockIn, Properties properties) {
        super(blockIn, properties);
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
}
