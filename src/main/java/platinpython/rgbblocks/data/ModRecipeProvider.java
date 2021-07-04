package platinpython.rgbblocks.data;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
	private final CompoundNBT whiteNBT = new CompoundNBT();

	public ModRecipeProvider(DataGenerator generator) {
		super(generator);
		whiteNBT.putInt("color", -1);
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapelessNBTRecipeBuilder.shapeless(ItemRegistry.PAINT_BUCKET.get(), 1, whiteNBT).makeNoReturnRecipe().requires(Tags.Items.DYES_RED).requires(Tags.Items.DYES_GREEN).requires(Tags.Items.DYES_BLUE).requires(Items.WATER_BUCKET).unlockedBy("has_water_bucket", has(Items.WATER_BUCKET)).save(consumer);

		blockIItemProvider(consumer, BlockRegistry.RGB_CONCRETE_POWDER.get(), Blocks.WHITE_CONCRETE_POWDER);
		blockTag(consumer, BlockRegistry.RGB_GLASS.get(), Tags.Items.STAINED_GLASS);
		blockIItemProvider(consumer, BlockRegistry.RGB_GLOWSTONE.get(), Blocks.GLOWSTONE);
		blockTag(consumer, BlockRegistry.RGB_PLANKS.get(), ItemTags.PLANKS);
		blockIItemProvider(consumer, BlockRegistry.RGB_TERRACOTTA.get(), Blocks.WHITE_TERRACOTTA);
		blockTag(consumer, BlockRegistry.RGB_WOOL.get(), ItemTags.WOOL);

		slabBlock(consumer, BlockRegistry.RGB_CONCRETE_SLAB.get(), BlockRegistry.RGB_CONCRETE.get());
		slabBlock(consumer, BlockRegistry.RGB_GLASS_SLAB.get(), BlockRegistry.RGB_GLASS.get());
		slabBlock(consumer, BlockRegistry.RGB_PLANKS_SLAB.get(), BlockRegistry.RGB_PLANKS.get());
		slabBlock(consumer, BlockRegistry.RGB_TERRACOTTA_SLAB.get(), BlockRegistry.RGB_TERRACOTTA.get());
		slabBlock(consumer, BlockRegistry.RGB_WOOL_SLAB.get(), BlockRegistry.RGB_WOOL.get());

		stairBlock(consumer, BlockRegistry.RGB_CONCRETE_STAIRS.get(), BlockRegistry.RGB_CONCRETE.get());
		stairBlock(consumer, BlockRegistry.RGB_GLASS_STAIRS.get(), BlockRegistry.RGB_GLASS.get());
		stairBlock(consumer, BlockRegistry.RGB_PLANKS_STAIRS.get(), BlockRegistry.RGB_PLANKS.get());
		stairBlock(consumer, BlockRegistry.RGB_TERRACOTTA_STAIRS.get(), BlockRegistry.RGB_TERRACOTTA.get());
		stairBlock(consumer, BlockRegistry.RGB_WOOL_STAIRS.get(), BlockRegistry.RGB_WOOL.get());

		ShapedNBTRecipeBuilder.shaped(BlockRegistry.RGB_ANTIBLOCK.get(), 8, whiteNBT).define('S', Tags.Items.STONE).define('G', BlockRegistry.RGB_GLOWSTONE.get()).pattern("SSS").pattern("SGS").pattern("SSS").unlockedBy("has_rgb_glowstone", has(BlockRegistry.RGB_GLOWSTONE.get())).save(consumer);
		ShapedNBTRecipeBuilder.shaped(BlockRegistry.RGB_CARPET.get()).define('#', BlockRegistry.RGB_WOOL.get()).pattern("##").unlockedBy("has_rgb_wool", has(BlockRegistry.RGB_WOOL.get())).save(consumer);
		ShapedNBTRecipeBuilder.shaped(BlockRegistry.RGB_REDSTONE_LAMP.get(), 1, whiteNBT).define('R', Tags.Items.DUSTS_REDSTONE).define('G', BlockRegistry.RGB_GLOWSTONE.get()).pattern(" R ").pattern("RGR").pattern(" R ").unlockedBy("has_rgb_glowstone", has(BlockRegistry.RGB_GLOWSTONE.get())).save(consumer);
	}

	private void blockIItemProvider(Consumer<IFinishedRecipe> consumer, Block result, IItemProvider provider) {
		block(consumer, result, Ingredient.of(provider));
	}

	private void blockTag(Consumer<IFinishedRecipe> consumer, Block result, ITag<Item> tag) {
		block(consumer, result, Ingredient.of(tag));
	}

	private void block(Consumer<IFinishedRecipe> consumer, Block result, Ingredient ingredient) {
		ShapelessNBTRecipeBuilder.shapeless(result.getBlock(), 1, whiteNBT).requires(ingredient).requires(ItemRegistry.PAINT_BUCKET.get()).unlockedBy("has_paint_bucket", has(ItemRegistry.PAINT_BUCKET.get())).save(consumer);
	}

	private void slabBlock(Consumer<IFinishedRecipe> consumer, Block result, Block base) {
		ShapedNBTRecipeBuilder.shaped(result.getBlock(), 6, whiteNBT).define('#', base.getBlock()).pattern("###").unlockedBy("has_rgb_" + base.getRegistryName().getPath(), has(base)).save(consumer);
	}

	private void stairBlock(Consumer<IFinishedRecipe> consumer, Block result, Block base) {
		ShapedNBTRecipeBuilder.shaped(result.getBlock(), 4, whiteNBT).define('#', base.getBlock()).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_rgb_" + base.getRegistryName().getPath(), has(base)).save(consumer);
	}
}
