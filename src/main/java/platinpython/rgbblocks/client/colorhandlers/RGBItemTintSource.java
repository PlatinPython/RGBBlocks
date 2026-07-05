package platinpython.rgbblocks.client.colorhandlers;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

public class RGBItemTintSource implements ItemTintSource {
    public static final MapCodec<RGBItemTintSource> MAP_CODEC = MapCodec.unit(RGBItemTintSource::new);

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return itemStack.getOrDefault(DataComponentRegistry.COLOR, -1);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
