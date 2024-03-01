package platinpython.rgbblocks.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private final CompoundTag whiteNBT = new CompoundTag();

    public ModRecipeProvider(PackOutput output) {
        super(output);
        whiteNBT.putInt("color", -1);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessNBTRecipeBuilder.shapeless(ItemRegistry.PAINT_BUCKET.get(), 1, whiteNBT)
            .makeNoReturnRecipe()
            .requires(Tags.Items.DYES_RED)
            .requires(Tags.Items.DYES_GREEN)
            .requires(Tags.Items.DYES_BLUE)
            .requires(Items.WATER_BUCKET)
            .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
            .save(consumer);

        ShapedNBTRecipeBuilder.shaped(BlockRegistry.RGB_CARPET.get(), 3, whiteNBT)
            .define('#', BlockRegistry.RGB_WOOL.get())
            .pattern("##")
            .unlockedBy("has_rgb_wool", has(BlockRegistry.RGB_WOOL.get()))
            .save(consumer);
        ShapedNBTRecipeBuilder.shaped(BlockRegistry.RGB_GLASS_PANE.get(), 16, whiteNBT)
            .define('#', BlockRegistry.RGB_GLASS.get())
            .pattern("###")
            .pattern("###")
            .unlockedBy("has_rgb_glass", has(BlockRegistry.RGB_GLASS.get()))
            .save(consumer);
        ShapedNBTRecipeBuilder.shaped(BlockRegistry.RGB_ANTIBLOCK.get(), 8, whiteNBT)
            .define('S', Tags.Items.STONE)
            .define('G', BlockRegistry.RGB_GLOWSTONE.get())
            .pattern("SSS")
            .pattern("SGS")
            .pattern("SSS")
            .unlockedBy("has_rgb_glowstone", has(BlockRegistry.RGB_GLOWSTONE.get()))
            .save(consumer);
        ShapedNBTRecipeBuilder.shaped(BlockRegistry.RGB_REDSTONE_LAMP.get(), 1, whiteNBT)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('G', BlockRegistry.RGB_GLOWSTONE.get())
            .pattern(" R ")
            .pattern("RGR")
            .pattern(" R ")
            .unlockedBy("has_rgb_glowstone", has(BlockRegistry.RGB_GLOWSTONE.get()))
            .save(consumer);

        blockIItemProvider(consumer, BlockRegistry.RGB_CONCRETE_POWDER.get(), Blocks.WHITE_CONCRETE_POWDER);
        blockTag(consumer, BlockRegistry.RGB_WOOL.get(), ItemTags.WOOL);
        blockTag(consumer, BlockRegistry.RGB_PLANKS.get(), ItemTags.PLANKS);
        blockIItemProvider(consumer, BlockRegistry.RGB_TERRACOTTA.get(), Blocks.WHITE_TERRACOTTA);
        blockTag(consumer, BlockRegistry.RGB_GLASS.get(), Tags.Items.STAINED_GLASS);
        blockIItemProvider(consumer, BlockRegistry.RGB_GLOWSTONE.get(), Blocks.GLOWSTONE);
        blockIItemProvider(consumer, BlockRegistry.RGB_PRISMARINE.get(), Blocks.PRISMARINE);
        blockIItemProvider(consumer, BlockRegistry.RGB_PRISMARINE_BRICKS.get(), Blocks.PRISMARINE_BRICKS);
        blockIItemProvider(consumer, BlockRegistry.RGB_DARK_PRISMARINE.get(), Blocks.DARK_PRISMARINE);
        blockIItemProvider(consumer, BlockRegistry.RGB_SEA_LANTERN.get(), Blocks.SEA_LANTERN);

        slabBlock(consumer, BlockRegistry.RGB_CONCRETE_SLAB, BlockRegistry.RGB_CONCRETE);
        slabBlock(consumer, BlockRegistry.RGB_WOOL_SLAB, BlockRegistry.RGB_WOOL);
        slabBlock(consumer, BlockRegistry.RGB_PLANKS_SLAB, BlockRegistry.RGB_PLANKS);
        slabBlock(consumer, BlockRegistry.RGB_TERRACOTTA_SLAB, BlockRegistry.RGB_TERRACOTTA);
        slabBlock(consumer, BlockRegistry.RGB_GLASS_SLAB, BlockRegistry.RGB_GLASS);
        slabBlock(consumer, BlockRegistry.RGB_PRISMARINE_SLAB, BlockRegistry.RGB_PRISMARINE);
        slabBlock(consumer, BlockRegistry.RGB_PRISMARINE_BRICK_SLAB, BlockRegistry.RGB_PRISMARINE_BRICKS);
        slabBlock(consumer, BlockRegistry.RGB_DARK_PRISMARINE_SLAB, BlockRegistry.RGB_DARK_PRISMARINE);

        stairBlock(consumer, BlockRegistry.RGB_CONCRETE_STAIRS, BlockRegistry.RGB_CONCRETE);
        stairBlock(consumer, BlockRegistry.RGB_WOOL_STAIRS, BlockRegistry.RGB_WOOL);
        stairBlock(consumer, BlockRegistry.RGB_PLANKS_STAIRS, BlockRegistry.RGB_PLANKS);
        stairBlock(consumer, BlockRegistry.RGB_TERRACOTTA_STAIRS, BlockRegistry.RGB_TERRACOTTA);
        stairBlock(consumer, BlockRegistry.RGB_GLASS_STAIRS, BlockRegistry.RGB_GLASS);
        stairBlock(consumer, BlockRegistry.RGB_PRISMARINE_STAIRS, BlockRegistry.RGB_PRISMARINE);
        stairBlock(consumer, BlockRegistry.RGB_PRISMARINE_BRICK_STAIRS, BlockRegistry.RGB_PRISMARINE_BRICKS);
        stairBlock(consumer, BlockRegistry.RGB_DARK_PRISMARINE_STAIRS, BlockRegistry.RGB_DARK_PRISMARINE);

        RegistryHandler.BLOCKS.getEntries()
            .forEach(
                (block) -> ShapelessNBTRecipeBuilder.shapeless(block.get().asItem())
                    .requires(block.get())
                    .requires(ItemRegistry.PAINT_BUCKET.get())
                    .unlockedBy(
                        "has_paint_bucket_and_" + block.getId().getPath(),
                        inventoryTrigger(
                            ItemPredicate.Builder.item().of(ItemRegistry.PAINT_BUCKET.get()).build(),
                            ItemPredicate.Builder.item().of(block.get()).build()
                        )
                    )
                    .save(consumer, block.getId() + "_coloring")
            );
    }

    private void blockIItemProvider(Consumer<FinishedRecipe> consumer, Block result, ItemLike provider) {
        block(consumer, result, Ingredient.of(provider));
    }

    private void blockTag(Consumer<FinishedRecipe> consumer, Block result, TagKey<Item> tag) {
        block(consumer, result, Ingredient.of(tag));
    }

    private void block(Consumer<FinishedRecipe> consumer, Block result, Ingredient ingredient) {
        ShapelessNBTRecipeBuilder.shapeless(result.asItem())
            .requires(ingredient)
            .requires(ItemRegistry.PAINT_BUCKET.get())
            .unlockedBy("has_paint_bucket", has(ItemRegistry.PAINT_BUCKET.get()))
            .save(consumer);
    }

    private void slabBlock(
        Consumer<FinishedRecipe> consumer,
        RegistryObject<? extends Block> result,
        RegistryObject<? extends Block> base
    ) {
        ShapedNBTRecipeBuilder.shaped(result.get().asItem(), 6, whiteNBT)
            .define('#', base.get().asItem())
            .pattern("###")
            .unlockedBy("has_rgb_" + base.getId().getPath(), has(base.get()))
            .save(consumer);
    }

    private void stairBlock(
        Consumer<FinishedRecipe> consumer,
        RegistryObject<? extends Block> result,
        RegistryObject<? extends Block> base
    ) {
        ShapedNBTRecipeBuilder.shaped(result.get().asItem(), 4, whiteNBT)
            .define('#', base.get().asItem())
            .pattern("#  ")
            .pattern("## ")
            .pattern("###")
            .unlockedBy("has_rgb_" + base.getId().getPath(), has(base.get()))
            .save(consumer);
    }
}
