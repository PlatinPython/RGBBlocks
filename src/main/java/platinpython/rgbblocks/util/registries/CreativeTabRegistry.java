package platinpython.rgbblocks.util.registries;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.RegistryHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreativeTabRegistry {
    public static final RegistryObject<CreativeModeTab> TAB =
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
                        .map(RegistryObject::get)
                        .map(Item::getDefaultInstance)
                        .toList()
                );
                items.sort(
                    (i1, i2) -> Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(i1.getItem()))
                        .compareNamespaced(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(i2.getItem())))
                );
                items.removeIf(i -> i.getItem().equals(ItemRegistry.PAINT_BUCKET.get()));
                items.add(0, ItemRegistry.PAINT_BUCKET.get().getDefaultInstance());
                output.acceptAll(items);
            })
            .build();
    }

    public static void register() {}
}
