package platinpython.rgbblocks.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

import java.util.List;

public class ShapelessNoReturnRecipe extends ShapelessRecipe {
    public static final MapCodec<ShapelessNoReturnRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance
            .group(
                CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                CraftingBookInfo.MAP_CODEC.forGetter(recipe -> recipe.bookInfo),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(ShapelessRecipe::result),
                Codec
                    .lazyInitialized(
                        () -> Ingredient.CODEC
                            .listOf(1, ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth())
                    )
                    .fieldOf("ingredients")
                    .forGetter(recipe -> recipe.ingredients)
            )
            .apply(instance, ShapelessNoReturnRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessNoReturnRecipe> STREAM_CODEC =
        StreamCodec.composite(
            // spotless:off
            CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            CraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
            ItemStackTemplate.STREAM_CODEC, ShapelessRecipe::result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), recipe -> recipe.ingredients,
            ShapelessNoReturnRecipe::new
            // spotless:on
        );

    private final List<Ingredient> ingredients;

    public ShapelessNoReturnRecipe(
        CommonInfo commonInfo,
        CraftingBookInfo bookInfo,
        ItemStackTemplate result,
        List<Ingredient> ingredients
    ) {
        super(commonInfo, bookInfo, result, ingredients);
        this.ingredients = ingredients;
    }

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        return (RecipeSerializer) RecipeSerializerRegistry.SHAPELESS_NO_RETURN_RECIPE.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput) {
        return NonNullList.withSize(craftingInput.size(), ItemStack.EMPTY);
    }
}
