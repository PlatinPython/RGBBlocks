package platinpython.rgbblocks.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ShapelessNBTRecipeBuilder {
    private final Item result;
    private final int count;
    private final CompoundTag compound;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;
    private boolean isNoReturnRecipe = false;

    private ShapelessNBTRecipeBuilder(ItemLike result, int count, CompoundTag compound) {
        this.result = result.asItem();
        this.count = count;
        this.compound = compound;
    }

    public static ShapelessNBTRecipeBuilder shapeless(ItemLike result) {
        return new ShapelessNBTRecipeBuilder(result, 1, null);
    }

    public static ShapelessNBTRecipeBuilder shapeless(ItemLike result, int count) {
        return new ShapelessNBTRecipeBuilder(result, count, null);
    }

    public static ShapelessNBTRecipeBuilder shapeless(ItemLike result, int count, CompoundTag compound) {
        return new ShapelessNBTRecipeBuilder(result, count, compound);
    }

    public ShapelessNBTRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public ShapelessNBTRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public ShapelessNBTRecipeBuilder requires(ItemLike item, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }

        return this;
    }

    public ShapelessNBTRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public ShapelessNBTRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }

        return this;
    }

    public ShapelessNBTRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    public ShapelessNBTRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.save(consumer, ForgeRegistries.ITEMS.getKey(this.result));
    }

    public void save(Consumer<FinishedRecipe> consumer, String save) {
        ResourceLocation saveTo = new ResourceLocation(save);
        if (saveTo.equals(ForgeRegistries.ITEMS.getKey(this.result))) {
            throw new IllegalStateException("Shapeless Recipe " + save + " should remove its 'save' argument");
        } else {
            this.save(consumer, saveTo);
        }
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        this.ensureValid(id);
        this.advancement.parent(new ResourceLocation("recipes/root"))
                        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                        .rewards(AdvancementRewards.Builder.recipe(id))
                        .requirements(RequirementsStrategy.OR);
        consumer.accept(new ShapelessNBTRecipeBuilder.Result(id,
                                                             this.result,
                                                             this.count,
                                                             this.compound,
                                                             this.group == null ? "" : this.group,
                                                             this.ingredients,
                                                             this.advancement,
                                                             new ResourceLocation(id.getNamespace(),
                                                                                  "recipes/" +
                                                                                  this.result.getItemCategory()
                                                                                             .getRecipeFolderName() +
                                                                                  "/" +
                                                                                  id.getPath()),
                                                             this.isNoReturnRecipe));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public ShapelessNBTRecipeBuilder makeNoReturnRecipe() {
        isNoReturnRecipe = true;
        return this;
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final CompoundTag compound;
        private final String group;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final boolean isNoReturnRecipe;

        public Result(ResourceLocation id, Item result, int count, CompoundTag compound, String group,
                      List<Ingredient> ingredients, Advancement.Builder advancementBuilder,
                      ResourceLocation advancementId, boolean isNoReturnRecipe) {
            this.id = id;
            this.result = result;
            this.count = count;
            this.compound = compound;
            this.group = group;
            this.ingredients = ingredients;
            this.advancement = advancementBuilder;
            this.advancementId = advancementId;
            this.isNoReturnRecipe = isNoReturnRecipe;
        }

        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            JsonArray ingredientsJson = new JsonArray();

            for (Ingredient ingredient : this.ingredients) {
                ingredientsJson.add(ingredient.toJson());
            }

            json.add("ingredients", ingredientsJson);
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
            if (this.count > 1) {
                resultJson.addProperty("count", this.count);
            }
            if (this.compound != null) {
                resultJson.addProperty("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, this.compound).toString());
            }

            json.add("result", resultJson);
        }

        public RecipeSerializer<?> getType() {
            return isNoReturnRecipe
                   ? RecipeSerializerRegistry.SHAPELESS_NO_RETURN_RECIPE.get()
                   : RecipeSerializerRegistry.SHAPELESS_DURABILITY_AWARE_PAINT_BUCKET_RECIPE.get();
        }

        public ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
