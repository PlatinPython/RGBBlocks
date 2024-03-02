package platinpython.rgbblocks.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import platinpython.rgbblocks.item.crafting.ShapelessDurabilityAwarePaintBucketRecipe;
import platinpython.rgbblocks.item.crafting.ShapelessNoReturnRecipe;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SpecialShapelessRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final ItemStack resultStack;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private @Nullable String group;
    private boolean isNoReturnRecipe = false;

    public SpecialShapelessRecipeBuilder(RecipeCategory category, ItemStack result) {
        this.category = category;
        this.resultStack = result;
    }

    public static SpecialShapelessRecipeBuilder shapeless(RecipeCategory category, ItemStack result) {
        return new SpecialShapelessRecipeBuilder(category, result);
    }

    public SpecialShapelessRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public SpecialShapelessRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public SpecialShapelessRecipeBuilder requires(ItemLike item, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }

        return this;
    }

    public SpecialShapelessRecipeBuilder requires(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public SpecialShapelessRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
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
    public Item getResult() {
        return this.resultStack.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        this.ensureValid(id);
        Advancement.Builder builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        ShapelessRecipe shapelessRecipe;
        if (isNoReturnRecipe) {
            shapelessRecipe = new ShapelessNoReturnRecipe(
                Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category),
                this.resultStack, this.ingredients
            );
        } else {
            shapelessRecipe = new ShapelessDurabilityAwarePaintBucketRecipe(
                Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category),
                this.resultStack, this.ingredients
            );
        }
        recipeOutput.accept(
            id, shapelessRecipe, builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/"))
        );
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}
