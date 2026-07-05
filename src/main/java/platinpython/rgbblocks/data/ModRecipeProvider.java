package platinpython.rgbblocks.data;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        SpecialShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, ItemRegistry.PAINT_BUCKET)
            .makeNoReturnRecipe()
            .requires(Tags.Items.DYES_RED)
            .requires(Tags.Items.DYES_GREEN)
            .requires(Tags.Items.DYES_BLUE)
            .requires(Items.WATER_BUCKET)
            .unlockedBy(getHasName(Items.WATER_BUCKET), this.has(Items.WATER_BUCKET))
            .save(this.output);

        block(BlockRegistry.RGB_CONCRETE_POWDER.get(), Blocks.WHITE_CONCRETE_POWDER);
        block(BlockRegistry.RGB_WOOL.get(), ItemTags.WOOL);
        block(BlockRegistry.RGB_PLANKS.get(), ItemTags.PLANKS);
        block(BlockRegistry.RGB_TERRACOTTA.get(), Blocks.WHITE_TERRACOTTA);
        block(
            BlockRegistry.RGB_GLASS.get(),
            IntersectionIngredient.of(
                Ingredient.of(this.items.getOrThrow(Tags.Items.GLASS_BLOCKS)),
                Ingredient.of(this.items.getOrThrow(Tags.Items.DYED))
            )
        );
        block(BlockRegistry.RGB_GLOWSTONE.get(), Blocks.GLOWSTONE);
        block(BlockRegistry.RGB_PRISMARINE.get(), Blocks.PRISMARINE);
        block(BlockRegistry.RGB_PRISMARINE_BRICKS.get(), Blocks.PRISMARINE_BRICKS);
        block(BlockRegistry.RGB_DARK_PRISMARINE.get(), Blocks.DARK_PRISMARINE);
        block(BlockRegistry.RGB_SEA_LANTERN.get(), Blocks.SEA_LANTERN);

        BlockFamilies.getAllFamilies().forEach(this::generateRecipes);

        this.carpet(BlockRegistry.RGB_CARPET, BlockRegistry.RGB_WOOL);
        this.stainedGlassPaneFromStainedGlass(BlockRegistry.RGB_GLASS_PANE, BlockRegistry.RGB_GLASS);
        this.shaped(RecipeCategory.DECORATIONS, BlockRegistry.RGB_ANTIBLOCK, 8)
            .define('S', Tags.Items.STONES)
            .define('G', BlockRegistry.RGB_GLOWSTONE)
            .pattern("SSS")
            .pattern("SGS")
            .pattern("SSS")
            .unlockedBy("has_rgb_glowstone", this.has(BlockRegistry.RGB_GLOWSTONE))
            .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, BlockRegistry.RGB_REDSTONE_LAMP)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('G', BlockRegistry.RGB_GLOWSTONE)
            .pattern(" R ")
            .pattern("RGR")
            .pattern(" R ")
            .unlockedBy("has_rgb_glowstone", this.has(BlockRegistry.RGB_GLOWSTONE))
            .save(this.output);

        RegistryHandler.BLOCKS.getEntries()
            .forEach(
                block -> SpecialShapelessRecipeBuilder
                    .shapeless(this.items, RecipeCategory.DECORATIONS, block.get().asItem())
                    .requires(block.get())
                    .requires(ItemRegistry.PAINT_BUCKET)
                    .unlockedBy(
                        "has_paint_bucket_and_" + block.getId().getPath(),
                        inventoryTrigger(
                            ItemPredicate.Builder.item().of(this.items, ItemRegistry.PAINT_BUCKET).build(),
                            ItemPredicate.Builder.item().of(this.items, block.get()).build()
                        )
                    )
                    .save(this.output, block.getId() + "_coloring")
            );
    }

    private void generateRecipes(BlockFamily blockFamily) {
        this.generateRecipes(blockFamily, FeatureFlags.DEFAULT_FLAGS);
    }

    private void block(ItemLike result, ItemLike provider) {
        block(result, Ingredient.of(provider));
    }

    private void block(ItemLike result, TagKey<Item> tag) {
        block(result, Ingredient.of(this.items.getOrThrow(tag)));
    }

    private void block(ItemLike result, Ingredient ingredient) {
        SpecialShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, result)
            .requires(ingredient)
            .requires(ItemRegistry.PAINT_BUCKET)
            .unlockedBy("has_paint_bucket", has(ItemRegistry.PAINT_BUCKET))
            .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {
        protected Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "RGBBlocks Recipes";
        }
    }
}
