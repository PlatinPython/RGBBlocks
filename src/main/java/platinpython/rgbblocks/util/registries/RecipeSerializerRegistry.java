package platinpython.rgbblocks.util.registries;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.item.crafting.ShapelessDurabilityAwarePaintBucketRecipe;
import platinpython.rgbblocks.item.crafting.ShapelessNoReturnRecipe;
import platinpython.rgbblocks.util.RegistryHandler;

public class RecipeSerializerRegistry {
    public static final RegistryObject<IRecipeSerializer<?>> SHAPELESS_NO_RETURN_RECIPE = RegistryHandler.RECIPE_SERIALIZERS.register(
            "crafting_shapeless_no_return",
            ShapelessNoReturnRecipe.Serializer::new);

    public static final RegistryObject<IRecipeSerializer<?>> SHAPELESS_DURABILITY_AWARE_PAINT_BUCKET_RECIPE = RegistryHandler.RECIPE_SERIALIZERS.register(
            "crafting_shapeless_durability_aware_paint_bucket",
            ShapelessDurabilityAwarePaintBucketRecipe.Serializer::new);

    public static void register() {
    }
}
