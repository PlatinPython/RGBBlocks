package platinpython.rgbblocks.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
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
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private final CompoundTag whiteNBT = new CompoundTag();

    public ModRecipeProvider(DataGenerator generator) {
        super(generator);
        whiteNBT.putInt("color", -1);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
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

        slabBlock(consumer, BlockRegistry.RGB_CONCRETE_SLAB.get(), BlockRegistry.RGB_CONCRETE.get());
        slabBlock(consumer, BlockRegistry.RGB_WOOL_SLAB.get(), BlockRegistry.RGB_WOOL.get());
        slabBlock(consumer, BlockRegistry.RGB_PLANKS_SLAB.get(), BlockRegistry.RGB_PLANKS.get());
        slabBlock(consumer, BlockRegistry.RGB_TERRACOTTA_SLAB.get(), BlockRegistry.RGB_TERRACOTTA.get());
        slabBlock(consumer, BlockRegistry.RGB_GLASS_SLAB.get(), BlockRegistry.RGB_GLASS.get());
        slabBlock(consumer, BlockRegistry.RGB_PRISMARINE_SLAB.get(), BlockRegistry.RGB_PRISMARINE.get());
        slabBlock(consumer, BlockRegistry.RGB_PRISMARINE_BRICK_SLAB.get(), BlockRegistry.RGB_PRISMARINE_BRICKS.get());
        slabBlock(consumer, BlockRegistry.RGB_DARK_PRISMARINE_SLAB.get(), BlockRegistry.RGB_DARK_PRISMARINE.get());

        stairBlock(consumer, BlockRegistry.RGB_CONCRETE_STAIRS.get(), BlockRegistry.RGB_CONCRETE.get());
        stairBlock(consumer, BlockRegistry.RGB_WOOL_STAIRS.get(), BlockRegistry.RGB_WOOL.get());
        stairBlock(consumer, BlockRegistry.RGB_PLANKS_STAIRS.get(), BlockRegistry.RGB_PLANKS.get());
        stairBlock(consumer, BlockRegistry.RGB_TERRACOTTA_STAIRS.get(), BlockRegistry.RGB_TERRACOTTA.get());
        stairBlock(consumer, BlockRegistry.RGB_GLASS_STAIRS.get(), BlockRegistry.RGB_GLASS.get());
        stairBlock(consumer, BlockRegistry.RGB_PRISMARINE_STAIRS.get(), BlockRegistry.RGB_PRISMARINE.get());
        stairBlock(consumer,
                   BlockRegistry.RGB_PRISMARINE_BRICK_STAIRS.get(),
                   BlockRegistry.RGB_PRISMARINE_BRICKS.get());
        stairBlock(consumer, BlockRegistry.RGB_DARK_PRISMARINE_STAIRS.get(), BlockRegistry.RGB_DARK_PRISMARINE.get());


        RegistryHandler.BLOCKS.getEntries()
                              .forEach((block) -> ShapelessNBTRecipeBuilder.shapeless(block.get().asItem())
                                                                           .requires(block.get())
                                                                           .requires(ItemRegistry.PAINT_BUCKET.get())
                                                                           .unlockedBy("has_paint_bucket_and_" +
                                                                                       block.getId().getPath(),
                                                                                       inventoryTrigger(ItemPredicate.Builder.item()
                                                                                                                             .of(ItemRegistry.PAINT_BUCKET.get())
                                                                                                                             .build(),
                                                                                                        ItemPredicate.Builder.item()
                                                                                                                             .of(block.get())
                                                                                                                             .build()))
                                                                           .save(consumer,
                                                                                 block.getId() + "_coloring"));
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

    private void slabBlock(Consumer<FinishedRecipe> consumer, Block result, Block base) {
        ShapedNBTRecipeBuilder.shaped(result.asItem(), 6, whiteNBT)
                              .define('#', base.asItem())
                              .pattern("###")
                              .unlockedBy("has_rgb_" + base.getRegistryName().getPath(), has(base))
                              .save(consumer);
    }

    private void stairBlock(Consumer<FinishedRecipe> consumer, Block result, Block base) {
        ShapedNBTRecipeBuilder.shaped(result.asItem(), 4, whiteNBT)
                              .define('#', base.asItem())
                              .pattern("#  ")
                              .pattern("## ")
                              .pattern("###")
                              .unlockedBy("has_rgb_" + base.getRegistryName().getPath(), has(base))
                              .save(consumer);
    }
}
