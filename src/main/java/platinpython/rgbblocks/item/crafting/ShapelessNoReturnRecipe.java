package platinpython.rgbblocks.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

import java.util.List;
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
    public NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput) {
        return NonNullList.withSize(craftingInput.size(), ItemStack.EMPTY);
    }

    public static class Serializer implements RecipeSerializer<ShapelessNoReturnRecipe> {
        private static final MapCodec<ShapelessNoReturnRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category")
                    .orElse(CraftingBookCategory.MISC)
                    .forGetter(ShapelessRecipe::category),
                ItemStack.STRICT_CODEC.fieldOf("result")
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

        private static final StreamCodec<RegistryFriendlyByteBuf, ShapelessNoReturnRecipe> STREAM_CODEC =
            StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapelessRecipe::getGroup,
                NeoForgeStreamCodecs.enumCodec(CraftingBookCategory.class), CraftingRecipe::category,
                ItemStack.STREAM_CODEC, recipe -> recipe.getResultItem(RegistryAccess.EMPTY),
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).map(NonNullList::copyOf, List::copyOf),
                ShapelessRecipe::getIngredients, ShapelessNoReturnRecipe::new
            );

        @Override
        public MapCodec<ShapelessNoReturnRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapelessNoReturnRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
