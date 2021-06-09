package platinpython.rgbblocks.data;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

public class ShapelessNBTRecipeBuilder {
	private final Item result;
	private final int count;
	private final CompoundNBT compound;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	private String group;
	private boolean isNoReturnRecipe = false;

	private ShapelessNBTRecipeBuilder(IItemProvider result, int count, CompoundNBT compound) {
		this.result = result.asItem();
		this.count = count;
		this.compound = compound;
	}

	public static ShapelessNBTRecipeBuilder shapeless(IItemProvider result) {
		return new ShapelessNBTRecipeBuilder(result, 1, null);
	}

	public static ShapelessNBTRecipeBuilder shapeless(IItemProvider result, int count) {
		return new ShapelessNBTRecipeBuilder(result, count, null);
	}

	public static ShapelessNBTRecipeBuilder shapeless(IItemProvider result, int count, CompoundNBT compound) {
		return new ShapelessNBTRecipeBuilder(result, count, compound);
	}

	public ShapelessNBTRecipeBuilder requires(ITag<Item> tag) {
		return this.requires(Ingredient.of(tag));
	}

	public ShapelessNBTRecipeBuilder requires(IItemProvider item) {
		return this.requires(item, 1);
	}

	public ShapelessNBTRecipeBuilder requires(IItemProvider item, int quantity) {
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

	public ShapelessNBTRecipeBuilder unlockedBy(String name, ICriterionInstance criterion) {
		this.advancement.addCriterion(name, criterion);
		return this;
	}

	public ShapelessNBTRecipeBuilder group(String group) {
		this.group = group;
		return this;
	}

	public void save(Consumer<IFinishedRecipe> consumer) {
		this.save(consumer, ForgeRegistries.ITEMS.getKey(this.result.getItem()));
	}

	public void save(Consumer<IFinishedRecipe> consumer, String save) {
		ResourceLocation saveTo = new ResourceLocation(save);
		if (saveTo.equals(ForgeRegistries.ITEMS.getKey(this.result.getItem()))) {
			throw new IllegalStateException("Shapeless Recipe " + save + " should remove its 'save' argument");
		} else {
			this.save(consumer, saveTo);
		}
	}

	public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
		consumer.accept(new ShapelessNBTRecipeBuilder.Result(id, this.result, this.count, this.compound,
				this.group == null ? "" : this.group, this.ingredients, this.advancement,
				new ResourceLocation(id.getNamespace(),
						"recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + id.getPath()),
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

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final Item result;
		private final int count;
		private final CompoundNBT compound;
		private final String group;
		private final List<Ingredient> ingredients;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;
		private final boolean isNoReturnRecipe;

		public Result(ResourceLocation id, Item result, int count, CompoundNBT compound, String group,
				List<Ingredient> ingredients, Advancement.Builder advancementBuilder, ResourceLocation advancementId,
				boolean isNoReturnRecipe) {
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
				resultJson.addProperty("nbt",
						NBTDynamicOps.INSTANCE.convertTo(JsonOps.INSTANCE, this.compound).toString());
			}

			json.add("result", resultJson);
		}

		public IRecipeSerializer<?> getType() {
			return isNoReturnRecipe ? RecipeSerializerRegistry.SHAPELESS_NO_RETURN_RECIPE.get()
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
