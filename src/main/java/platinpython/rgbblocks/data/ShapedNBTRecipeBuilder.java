package platinpython.rgbblocks.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

public class ShapedNBTRecipeBuilder {
    private final Item result;
    private final int count;
    private final CompoundTag compound;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;

    public ShapedNBTRecipeBuilder(ItemLike result, int count, CompoundTag compound) {
        this.result = result.asItem();
        this.count = count;
        this.compound = compound;
    }

    public static ShapedNBTRecipeBuilder shaped(ItemLike result) {
        return new ShapedNBTRecipeBuilder(result, 1, null);
    }

    public static ShapedNBTRecipeBuilder shaped(ItemLike result, int count) {
        return new ShapedNBTRecipeBuilder(result, count, null);
    }

    public static ShapedNBTRecipeBuilder shaped(ItemLike result, int count, CompoundTag compound) {
        return new ShapedNBTRecipeBuilder(result, count, compound);
    }

    public ShapedNBTRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return this.define(symbol, Ingredient.of(tag));
    }

    public ShapedNBTRecipeBuilder define(Character symbol, ItemLike item) {
        return this.define(symbol, Ingredient.of(item));
    }

    public ShapedNBTRecipeBuilder define(Character symbol, Ingredient item) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, item);
            return this;
        }
    }

    public ShapedNBTRecipeBuilder pattern(String pattern) {
        if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pattern);
            return this;
        }
    }

    public ShapedNBTRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    public ShapedNBTRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.save(consumer, ForgeRegistries.ITEMS.getKey(this.result));
    }

    public void save(Consumer<FinishedRecipe> consumer, String save) {
        ResourceLocation saveTo = new ResourceLocation(save);
        if (saveTo.equals(ForgeRegistries.ITEMS.getKey(this.result))) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
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
        consumer.accept(new ShapedNBTRecipeBuilder.Result(id, this.result, this.count, this.compound,
                                                          this.group == null ? "" : this.group, this.rows, this.key,
                                                          this.advancement, new ResourceLocation(id.getNamespace(),
                                                                                                 "recipes/" + this.result.getItemCategory()
                                                                                                                         .getRecipeFolderName() + "/" + id.getPath()
        )
        ));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for (String s : this.rows) {
                for (int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException(
                                "Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
                throw new IllegalStateException(
                        "Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (this.advancement.getCriteria().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }

    public class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final CompoundTag compound;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, int count, CompoundTag compound, String group,
                      List<String> pattern, Map<Character, Ingredient> key, Advancement.Builder advancement,
                      ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.count = count;
            this.compound = compound;
            this.group = group;
            this.pattern = pattern;
            this.key = key;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            JsonArray patternJson = new JsonArray();

            for (String s : this.pattern) {
                patternJson.add(s);
            }

            json.add("pattern", patternJson);
            JsonObject keyJson = new JsonObject();

            for (Entry<Character, Ingredient> entry : this.key.entrySet()) {
                keyJson.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }

            json.add("key", keyJson);
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
            return RecipeSerializer.SHAPED_RECIPE;
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
