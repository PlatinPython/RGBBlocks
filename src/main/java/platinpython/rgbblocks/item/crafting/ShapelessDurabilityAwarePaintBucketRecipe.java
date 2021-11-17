package platinpython.rgbblocks.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;

public class ShapelessDurabilityAwarePaintBucketRecipe extends ShapelessRecipe {
    public ShapelessDurabilityAwarePaintBucketRecipe(ResourceLocation id, String group, ItemStack result,
                                                     NonNullList<Ingredient> ingredients) {
        super(id, group, result, ingredients);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.SHAPELESS_DURABILITY_AWARE_PAINT_BUCKET_RECIPE.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer craftingInventory) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(craftingInventory.getContainerSize(),
                                                                  ItemStack.EMPTY);
        ItemStack blockStack = ItemStack.EMPTY;

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack item = craftingInventory.getItem(i);
            if (item.getItem() instanceof RGBBlockItem) {
                blockStack = item;
                break;
            }
        }

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack item = craftingInventory.getItem(i);
            if (item.getItem() instanceof PaintBucketItem) {
                if (item.getOrCreateTag().getInt("color") == blockStack.getOrCreateTag().getInt("color")) {
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
    public ItemStack assemble(CraftingContainer craftingInventory) {
        int color = 0;
        for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
            if (craftingInventory.getItem(i).getItem() instanceof PaintBucketItem) {
                color = craftingInventory.getItem(i).getOrCreateTag().getInt("color");
                break;
            }
        }
        ItemStack result = super.assemble(craftingInventory);
        result.getOrCreateTag().putInt("color", color);
        return result;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ShapelessDurabilityAwarePaintBucketRecipe> {
        @Override
        public ShapelessDurabilityAwarePaintBucketRecipe fromJson(ResourceLocation resourceLocation,
                                                                  JsonObject jsonObject) {
            String s = GsonHelper.getAsString(jsonObject, "group", "");
            NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + 9);
            } else {
                ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
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
        public ShapelessDurabilityAwarePaintBucketRecipe fromNetwork(ResourceLocation resourceLocation,
                                                                     FriendlyByteBuf buffer) {
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
        public void toNetwork(FriendlyByteBuf buffer, ShapelessDurabilityAwarePaintBucketRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
