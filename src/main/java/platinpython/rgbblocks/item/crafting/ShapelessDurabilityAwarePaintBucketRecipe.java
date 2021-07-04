package platinpython.rgbblocks.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

public class ShapelessDurabilityAwarePaintBucketRecipe extends ShapelessRecipe {
	public ShapelessDurabilityAwarePaintBucketRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		super(id, group, result, ingredients);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipeSerializerRegistry.SHAPELESS_DURABILITY_AWARE_PAINT_BUCKET_RECIPE.get();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory craftingInventory) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(craftingInventory.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack item = craftingInventory.getItem(i);
			if (item.getItem() instanceof PaintBucketItem) {
				if (item.getDamageValue() == item.getMaxDamage() - 1) {
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

	public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessDurabilityAwarePaintBucketRecipe> {
		@Override
		public ShapelessDurabilityAwarePaintBucketRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
			String s = JSONUtils.getAsString(jsonObject, "group", "");
			NonNullList<Ingredient> nonnulllist = itemsFromJson(JSONUtils.getAsJsonArray(jsonObject, "ingredients"));
			if (nonnulllist.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else if (nonnulllist.size() > 9) {
				throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + 9);
			} else {
				ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(jsonObject, "result"));
				return new ShapelessDurabilityAwarePaintBucketRecipe(resourceLocation, s, itemstack, nonnulllist);
			}
		}

		private static NonNullList<Ingredient> itemsFromJson(JsonArray jsonArray) {
			NonNullList<Ingredient> nonnulllist = NonNullList.create();

			for (int i = 0; i < jsonArray.size(); ++i) {
				Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
				if (!ingredient.isEmpty()) {
					nonnulllist.add(ingredient);
				}
			}
			return nonnulllist;
		}

		@Override
		public ShapelessDurabilityAwarePaintBucketRecipe fromNetwork(ResourceLocation resourceLocation, PacketBuffer buffer) {
			String s = buffer.readUtf(32767);
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

			for (int j = 0; j < nonnulllist.size(); ++j) {
				nonnulllist.set(j, Ingredient.fromNetwork(buffer));
			}

			ItemStack itemstack = buffer.readItem();
			return new ShapelessDurabilityAwarePaintBucketRecipe(resourceLocation, s, itemstack, nonnulllist);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, ShapelessDurabilityAwarePaintBucketRecipe recipe) {
			buffer.writeUtf(recipe.getGroup());
			buffer.writeVarInt(recipe.getIngredients().size());

			for (Ingredient ingredient : recipe.getIngredients()) {
				ingredient.toNetwork(buffer);
			}

			buffer.writeItem(recipe.getResultItem());
		}
	}
}
