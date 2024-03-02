package platinpython.rgbblocks.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

import java.util.function.Function;

public class ShapelessNoReturnRecipe extends ShapelessRecipe {
    public ShapelessNoReturnRecipe(
        String group,
        CraftingBookCategory category,
        ItemStack result,
        NonNullList<Ingredient> ingredients
    ) {
        super(group, category, result, ingredients);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.SHAPELESS_NO_RETURN_RECIPE.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer craftingInventory) {
        return NonNullList.withSize(craftingInventory.getContainerSize(), ItemStack.EMPTY);
    }

    public static class Serializer implements RecipeSerializer<ShapelessNoReturnRecipe> {
        private static final Codec<ShapelessNoReturnRecipe> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(ShapelessRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category")
                    .orElse(CraftingBookCategory.MISC)
                    .forGetter(ShapelessRecipe::category),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result")
                    .forGetter(recipe -> recipe.getResultItem(RegistryAccess.EMPTY)),
                Ingredient.CODEC_NONEMPTY.listOf().comapFlatMap(list -> {
                    if (list.isEmpty()) {
                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                    }
                    return list.size() > ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()
                        ? DataResult.error(
                            () -> "Too many ingredients for shapeless recipe. The maximum is: %s"
                                .formatted(ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth())
                        )
                        : DataResult.success(NonNullList.of(Ingredient.EMPTY, list.toArray(Ingredient[]::new)));
                }, Function.identity()).fieldOf("ingredients").forGetter(ShapelessRecipe::getIngredients)
            ).apply(instance, ShapelessNoReturnRecipe::new)
        );

        @Override
        public Codec<ShapelessNoReturnRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapelessNoReturnRecipe fromNetwork(FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            CraftingBookCategory craftingBookCategory = buffer.readEnum(CraftingBookCategory.class);
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buffer));
            ItemStack itemStack = buffer.readItem();
            return new ShapelessNoReturnRecipe(group, craftingBookCategory, itemStack, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapelessNoReturnRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            buffer.writeVarInt(recipe.getIngredients().size());
            recipe.getIngredients().forEach(i -> i.toNetwork(buffer));
            buffer.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
        }
    }
}
