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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

import java.util.List;

public class ShapelessDurabilityAwarePaintBucketRecipe extends ShapelessRecipe {
    public static final MapCodec<ShapelessDurabilityAwarePaintBucketRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
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
            .apply(instance, ShapelessDurabilityAwarePaintBucketRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessDurabilityAwarePaintBucketRecipe> STREAM_CODEC =
        StreamCodec.composite(
            // spotless:off
            CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            CraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
            ItemStackTemplate.STREAM_CODEC, ShapelessRecipe::result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), recipe -> recipe.ingredients,
            ShapelessDurabilityAwarePaintBucketRecipe::new
            // spotless:on
        );

    private final List<Ingredient> ingredients;

    public ShapelessDurabilityAwarePaintBucketRecipe(
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
        return (RecipeSerializer) RecipeSerializerRegistry.SHAPELESS_DURABILITY_AWARE_PAINT_BUCKET_RECIPE.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(craftingInput.size(), ItemStack.EMPTY);
        ItemStack blockStack = ItemStack.EMPTY;

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack item = craftingInput.getItem(i);
            if (item.getItem() instanceof RGBBlockItem) {
                blockStack = item;
                break;
            }
        }

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack item = craftingInput.getItem(i);
            if (item.getItem() instanceof PaintBucketItem) {
                if (item.getOrDefault(DataComponentRegistry.COLOR, -1)
                    .equals(blockStack.getOrDefault(DataComponentRegistry.COLOR, -1))) {
                    nonnulllist.set(i, item.copy());
                } else if (item.getDamageValue() == item.getMaxDamage() - 1) {
                    nonnulllist.set(i, new ItemStack(Items.BUCKET));
                } else {
                    ItemStack remainder = item.copy();
                    remainder.setDamageValue(item.getDamageValue() + 1);
                    nonnulllist.set(i, remainder);
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        int color = 0;
        for (int i = 0; i < input.size(); i++) {
            if (input.getItem(i).getItem() instanceof PaintBucketItem) {
                color = input.getItem(i).getOrDefault(DataComponentRegistry.COLOR, -1);
                break;
            }
        }
        ItemStack result = super.assemble(input);
        result.set(DataComponentRegistry.COLOR, color);
        return result;
    }
}
