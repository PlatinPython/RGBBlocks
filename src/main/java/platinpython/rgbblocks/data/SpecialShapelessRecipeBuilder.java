package platinpython.rgbblocks.data;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import platinpython.rgbblocks.item.crafting.ShapelessDurabilityAwarePaintBucketRecipe;
import platinpython.rgbblocks.item.crafting.ShapelessNoReturnRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpecialShapelessRecipeBuilder implements RecipeBuilder {
    private final HolderGetter<Item> items;
    private final RecipeCategory category;
    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private @org.jspecify.annotations.Nullable String group;
    private boolean isNoReturnRecipe = false;

    private SpecialShapelessRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result) {
        this.items = items;
        this.category = category;
        this.result = result;
    }

    public static SpecialShapelessRecipeBuilder shapeless(
        HolderGetter<Item> items,
        RecipeCategory category,
        ItemLike item
    ) {
        return shapeless(items, category, item, 1);
    }

    public static SpecialShapelessRecipeBuilder shapeless(
        HolderGetter<Item> items,
        RecipeCategory category,
        ItemLike item,
        int count
    ) {
        return new SpecialShapelessRecipeBuilder(items, category, new ItemStackTemplate(item.asItem(), count));
    }

    public SpecialShapelessRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(this.items.getOrThrow(tag)));
    }

    public SpecialShapelessRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public SpecialShapelessRecipeBuilder requires(ItemLike item, int count) {
        for (int i = 0; i < count; i++) {
            this.requires(Ingredient.of(item));
        }

        return this;
    }

    public SpecialShapelessRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public SpecialShapelessRecipeBuilder requires(Ingredient ingredient, int count) {
        for (int i = 0; i < count; i++) {
            this.ingredients.add(ingredient);
        }

        return this;
    }

    @Override
    public SpecialShapelessRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.advancementBuilder.unlockedBy(name, criterion);
        return this;
    }

    @Override
    public SpecialShapelessRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public SpecialShapelessRecipeBuilder makeNoReturnRecipe() {
        this.isNoReturnRecipe = true;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        ShapelessRecipe recipe;
        if (this.isNoReturnRecipe) {
            recipe = new ShapelessNoReturnRecipe(
                RecipeBuilder.createCraftingCommonInfo(true),
                RecipeBuilder.createCraftingBookInfo(this.category, this.group), this.result, this.ingredients
            );
        } else {
            recipe = new ShapelessDurabilityAwarePaintBucketRecipe(
                RecipeBuilder.createCraftingCommonInfo(true),
                RecipeBuilder.createCraftingBookInfo(this.category, this.group), this.result, this.ingredients
            );
        }
        output.accept(id, recipe, this.advancementBuilder.build(output, id, this.category));
    }
}
