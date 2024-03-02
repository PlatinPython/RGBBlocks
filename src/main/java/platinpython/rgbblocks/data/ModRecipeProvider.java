package platinpython.rgbblocks.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredBlock;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

import java.util.Optional;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private final CompoundTag whiteNBT = new CompoundTag();

    public ModRecipeProvider(PackOutput output) {
        super(output);
        whiteNBT.putInt("color", -1);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        SpecialShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, new ItemStack(ItemRegistry.PAINT_BUCKET.get(), 1, Optional.of(whiteNBT)))
            .makeNoReturnRecipe()
            .requires(Tags.Items.DYES_RED)
            .requires(Tags.Items.DYES_GREEN)
            .requires(Tags.Items.DYES_BLUE)
            .requires(Items.WATER_BUCKET)
            .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.DECORATIONS, new ItemStack(BlockRegistry.RGB_CARPET, 3, Optional.of(whiteNBT)))
            .define('#', BlockRegistry.RGB_WOOL.get())
            .pattern("##")
            .unlockedBy("has_rgb_wool", has(BlockRegistry.RGB_WOOL.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.DECORATIONS, new ItemStack(BlockRegistry.RGB_GLASS_PANE, 16, Optional.of(whiteNBT)))
            .define('#', BlockRegistry.RGB_GLASS.get())
            .pattern("###")
            .pattern("###")
            .unlockedBy("has_rgb_glass", has(BlockRegistry.RGB_GLASS.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.DECORATIONS, new ItemStack(BlockRegistry.RGB_ANTIBLOCK, 8, Optional.of(whiteNBT)))
            .define('S', Tags.Items.STONE)
            .define('G', BlockRegistry.RGB_GLOWSTONE.get())
            .pattern("SSS")
            .pattern("SGS")
            .pattern("SSS")
            .unlockedBy("has_rgb_glowstone", has(BlockRegistry.RGB_GLOWSTONE.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder
            .shaped(
                RecipeCategory.DECORATIONS,
                new ItemStack(BlockRegistry.RGB_REDSTONE_LAMP.get(), 1, Optional.of(whiteNBT))
            )
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('G', BlockRegistry.RGB_GLOWSTONE.get())
            .pattern(" R ")
            .pattern("RGR")
            .pattern(" R ")
            .unlockedBy("has_rgb_glowstone", has(BlockRegistry.RGB_GLOWSTONE.get()))
            .save(recipeOutput);

        blockIItemProvider(recipeOutput, BlockRegistry.RGB_CONCRETE_POWDER.get(), Blocks.WHITE_CONCRETE_POWDER);
        blockTag(recipeOutput, BlockRegistry.RGB_WOOL.get(), ItemTags.WOOL);
        blockTag(recipeOutput, BlockRegistry.RGB_PLANKS.get(), ItemTags.PLANKS);
        blockIItemProvider(recipeOutput, BlockRegistry.RGB_TERRACOTTA.get(), Blocks.WHITE_TERRACOTTA);
        blockTag(recipeOutput, BlockRegistry.RGB_GLASS.get(), Tags.Items.STAINED_GLASS);
        blockIItemProvider(recipeOutput, BlockRegistry.RGB_GLOWSTONE.get(), Blocks.GLOWSTONE);
        blockIItemProvider(recipeOutput, BlockRegistry.RGB_PRISMARINE.get(), Blocks.PRISMARINE);
        blockIItemProvider(recipeOutput, BlockRegistry.RGB_PRISMARINE_BRICKS.get(), Blocks.PRISMARINE_BRICKS);
        blockIItemProvider(recipeOutput, BlockRegistry.RGB_DARK_PRISMARINE.get(), Blocks.DARK_PRISMARINE);
        blockIItemProvider(recipeOutput, BlockRegistry.RGB_SEA_LANTERN.get(), Blocks.SEA_LANTERN);

        slabBlock(recipeOutput, BlockRegistry.RGB_CONCRETE_SLAB, BlockRegistry.RGB_CONCRETE);
        slabBlock(recipeOutput, BlockRegistry.RGB_WOOL_SLAB, BlockRegistry.RGB_WOOL);
        slabBlock(recipeOutput, BlockRegistry.RGB_PLANKS_SLAB, BlockRegistry.RGB_PLANKS);
        slabBlock(recipeOutput, BlockRegistry.RGB_TERRACOTTA_SLAB, BlockRegistry.RGB_TERRACOTTA);
        slabBlock(recipeOutput, BlockRegistry.RGB_GLASS_SLAB, BlockRegistry.RGB_GLASS);
        slabBlock(recipeOutput, BlockRegistry.RGB_PRISMARINE_SLAB, BlockRegistry.RGB_PRISMARINE);
        slabBlock(recipeOutput, BlockRegistry.RGB_PRISMARINE_BRICK_SLAB, BlockRegistry.RGB_PRISMARINE_BRICKS);
        slabBlock(recipeOutput, BlockRegistry.RGB_DARK_PRISMARINE_SLAB, BlockRegistry.RGB_DARK_PRISMARINE);

        stairBlock(recipeOutput, BlockRegistry.RGB_CONCRETE_STAIRS, BlockRegistry.RGB_CONCRETE);
        stairBlock(recipeOutput, BlockRegistry.RGB_WOOL_STAIRS, BlockRegistry.RGB_WOOL);
        stairBlock(recipeOutput, BlockRegistry.RGB_PLANKS_STAIRS, BlockRegistry.RGB_PLANKS);
        stairBlock(recipeOutput, BlockRegistry.RGB_TERRACOTTA_STAIRS, BlockRegistry.RGB_TERRACOTTA);
        stairBlock(recipeOutput, BlockRegistry.RGB_GLASS_STAIRS, BlockRegistry.RGB_GLASS);
        stairBlock(recipeOutput, BlockRegistry.RGB_PRISMARINE_STAIRS, BlockRegistry.RGB_PRISMARINE);
        stairBlock(recipeOutput, BlockRegistry.RGB_PRISMARINE_BRICK_STAIRS, BlockRegistry.RGB_PRISMARINE_BRICKS);
        stairBlock(recipeOutput, BlockRegistry.RGB_DARK_PRISMARINE_STAIRS, BlockRegistry.RGB_DARK_PRISMARINE);

        RegistryHandler.BLOCKS.getEntries()
            .forEach(
                (block) -> SpecialShapelessRecipeBuilder
                    .shapeless(RecipeCategory.DECORATIONS, new ItemStack(block.get()))
                    .requires(block.get())
                    .requires(ItemRegistry.PAINT_BUCKET)
                    .unlockedBy(
                        "has_paint_bucket_and_" + block.getId().getPath(),
                        inventoryTrigger(
                            ItemPredicate.Builder.item().of(ItemRegistry.PAINT_BUCKET).build(),
                            ItemPredicate.Builder.item().of(block.get()).build()
                        )
                    )
                    .save(recipeOutput, block.getId() + "_coloring")
            );
    }

    private void blockIItemProvider(RecipeOutput recipeOutput, Block result, ItemLike provider) {
        block(recipeOutput, result, Ingredient.of(provider));
    }

    private void blockTag(RecipeOutput recipeOutput, Block result, TagKey<Item> tag) {
        block(recipeOutput, result, Ingredient.of(tag));
    }

    private void block(RecipeOutput recipeOutput, Block result, Ingredient ingredient) {
        SpecialShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, new ItemStack(result))
            .requires(ingredient)
            .requires(ItemRegistry.PAINT_BUCKET)
            .unlockedBy("has_paint_bucket", has(ItemRegistry.PAINT_BUCKET))
            .save(recipeOutput);
    }

    private void slabBlock(
        RecipeOutput recipeOutput,
        DeferredBlock<? extends Block> result,
        DeferredBlock<? extends Block> base
    ) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, new ItemStack(result, 6, Optional.of(whiteNBT)))
            .define('#', base)
            .pattern("###")
            .unlockedBy("has_rgb_" + base.getId().getPath(), has(base))
            .save(recipeOutput);
    }

    private void stairBlock(
        RecipeOutput recipeOutput,
        DeferredBlock<? extends Block> result,
        DeferredBlock<? extends Block> base
    ) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, new ItemStack(result, 4, Optional.of(whiteNBT)))
            .define('#', base)
            .pattern("#  ")
            .pattern("## ")
            .pattern("###")
            .unlockedBy("has_rgb_" + base.getId().getPath(), has(base))
            .save(recipeOutput);
    }
}
