package platinpython.rgbblocks.util.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.RegistryHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CreativeTabRegistry {
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
        RegistryHandler.CREATIVE_MODE_TABS.register("tab", CreativeTabRegistry::getTab);

    private static CreativeModeTab getTab() {
        return CreativeModeTab.builder()
            .title(Component.translatable("item_group." + RGBBlocks.MOD_ID + ".tab"))
            .icon(() -> {
                ItemStack stack = new ItemStack(ItemRegistry.PAINT_BUCKET.get());
                stack.getOrCreateTag().putInt("color", -1);
                return stack;
            })
            .displayItems((displayParameters, output) -> {
                List<ItemStack> items = new ArrayList<>(
                    RegistryHandler.ITEMS.getEntries()
                        .stream()
                        .map(Supplier::get)
                        .map(Item::getDefaultInstance)
                        .toList()
                );
                items.sort((i1, i2) -> {
                    if (i1.getItem() == ItemRegistry.PAINT_BUCKET.get()) {
                        return -1;
                    } else if (i2.getItem() == ItemRegistry.PAINT_BUCKET.get()) {
                        return 1;
                    } else {
                        return BuiltInRegistries.ITEM.getKey(i1.getItem())
                            .compareNamespaced(BuiltInRegistries.ITEM.getKey(i2.getItem()));
                    }
                });
                output.acceptAll(items);
            })
            .build();
    }

    public static void register() {}
}
