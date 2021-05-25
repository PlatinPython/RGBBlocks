package platinpython.rgbblocks.util.registries;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.item.crafting.PaintbucketRecipe;
import platinpython.rgbblocks.util.RegistryHandler;

public class RecipeSerializerRegistry {
	public static final RegistryObject<IRecipeSerializer<?>> BUCKET_OF_PAINT_RECIPE = RegistryHandler.RECIPE_SERIALIZERS
			.register("bucket_of_paint_recipe", PaintbucketRecipe.Serializer::new);

	public static void register() {
	}
}
