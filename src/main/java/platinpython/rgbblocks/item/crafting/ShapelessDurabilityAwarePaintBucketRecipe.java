package platinpython.rgbblocks.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

import java.util.List;
import java.util.function.Function;

public class ShapelessDurabilityAwarePaintBucketRecipe extends ShapelessRecipe {
    public ShapelessDurabilityAwarePaintBucketRecipe(
        String group,
        CraftingBookCategory category,
        ItemStack result,
        NonNullList<Ingredient> ingredients
    ) {
        super(group, category, result, ingredients);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.SHAPELESS_DURABILITY_AWARE_PAINT_BUCKET_RECIPE.get();
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
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        int color = 0;
        for (int i = 0; i < craftingInput.size(); i++) {
            if (craftingInput.getItem(i).getItem() instanceof PaintBucketItem) {
                color = craftingInput.getItem(i).getOrDefault(DataComponentRegistry.COLOR, -1);
                break;
            }
        }
        ItemStack result = super.assemble(craftingInput, provider);
        result.set(DataComponentRegistry.COLOR, color);
        return result;
    }

    public static class Serializer implements RecipeSerializer<ShapelessDurabilityAwarePaintBucketRecipe> {
        private static final MapCodec<ShapelessDurabilityAwarePaintBucketRecipe> CODEC = RecordCodecBuilder.mapCodec(
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
            ).apply(instance, ShapelessDurabilityAwarePaintBucketRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, ShapelessDurabilityAwarePaintBucketRecipe> STREAM_CODEC =
            StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapelessRecipe::getGroup,
                NeoForgeStreamCodecs.enumCodec(CraftingBookCategory.class), CraftingRecipe::category,
                ItemStack.STREAM_CODEC, recipe -> recipe.getResultItem(RegistryAccess.EMPTY),
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).map(NonNullList::copyOf, List::copyOf),
                ShapelessRecipe::getIngredients, ShapelessDurabilityAwarePaintBucketRecipe::new
            );

        @Override
        public MapCodec<ShapelessDurabilityAwarePaintBucketRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapelessDurabilityAwarePaintBucketRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
