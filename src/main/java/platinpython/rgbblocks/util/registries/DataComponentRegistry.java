package platinpython.rgbblocks.util.registries;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.rgbblocks.util.RegistryHandler;

public class DataComponentRegistry {
    public static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COLOR =
        RegistryHandler.DATA_COMPONENTS.registerComponentType(
            "color", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
        );

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> RGB_SELECTED =
        RegistryHandler.DATA_COMPONENTS.registerComponentType(
            "rgb_selected", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
        );

    public static void register() {}
}
