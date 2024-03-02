package platinpython.rgbblocks.util.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.util.RegistryHandler;

import java.util.function.Supplier;

public class BlockEntityRegistry {
    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RGBBlockEntity>> RGB =
        RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "rgb",
            () -> BlockEntityType.Builder
                .of(
                    RGBBlockEntity::new,
                    RegistryHandler.BLOCKS.getEntries().stream().map(Supplier::get).toArray(Block[]::new)
                )
                .build(null)
        );

    public static void register() {}
}
