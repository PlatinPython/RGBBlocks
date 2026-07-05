package platinpython.rgbblocks.util.registries;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.rgbblocks.item.crafting.ShapelessDurabilityAwarePaintBucketRecipe;
import platinpython.rgbblocks.item.crafting.ShapelessNoReturnRecipe;
import platinpython.rgbblocks.util.RegistryHandler;

public class RecipeSerializerRegistry {
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessNoReturnRecipe>> SHAPELESS_NO_RETURN_RECIPE =
        RegistryHandler.RECIPE_SERIALIZERS.register(
            "crafting_shapeless_no_return",
            () -> new RecipeSerializer<>(ShapelessNoReturnRecipe.MAP_CODEC, ShapelessNoReturnRecipe.STREAM_CODEC)
        );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessDurabilityAwarePaintBucketRecipe>> SHAPELESS_DURABILITY_AWARE_PAINT_BUCKET_RECIPE =
        RegistryHandler.RECIPE_SERIALIZERS.register(
            "crafting_shapeless_durability_aware_paint_bucket",
            () -> new RecipeSerializer<>(
                ShapelessDurabilityAwarePaintBucketRecipe.MAP_CODEC,
                ShapelessDurabilityAwarePaintBucketRecipe.STREAM_CODEC
            )
        );

    public static void register() {}
}
