package platinpython.rgbblocks.util.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.util.RegistryHandler;

public class BlockEntityRegistry {
    @SuppressWarnings("DataFlowIssue")
    public static final RegistryObject<BlockEntityType<RGBBlockEntity>> RGB =
        RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "rgb",
            () -> BlockEntityType.Builder
                .of(
                    RGBBlockEntity::new,
                    RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new)
                )
                .build(null)
        );

    public static void register() {}
}
