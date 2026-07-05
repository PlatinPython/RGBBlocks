package platinpython.rgbblocks.util.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;
import platinpython.rgbblocks.util.RegistryHandler;

public class EntityRegistry {
    public static final DeferredHolder<EntityType<?>, EntityType<RGBFallingBlockEntity>> RGB_FALLING_BLOCK =
        RegistryHandler.ENTITY_TYPES.register(
            "rgb_falling_block",
            () -> EntityType.Builder.<RGBFallingBlockEntity>of(RGBFallingBlockEntity::new, MobCategory.MISC)
                .noLootTable()
                .sized(0.98F, 0.98F)
                .clientTrackingRange(10)
                .updateInterval(20)
                .setOnlyOpCanSetNbt(true)
                .build(
                    ResourceKey.create(
                        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "rgb_falling_block")
                    )
                )
        );

    public static void register() {}
}
