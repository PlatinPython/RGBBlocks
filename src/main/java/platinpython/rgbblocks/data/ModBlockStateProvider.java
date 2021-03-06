package platinpython.rgbblocks.data;

import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, RGBBlocks.MOD_ID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(BlockRegistry.RGB_CARPET.get(),
                    models().singleTexture(BlockRegistry.RGB_CARPET.getId().getPath(),
                                           modLoc(ModelProvider.BLOCK_FOLDER + "/thin_block"),
                                           "all",
                                           modLoc(ModelProvider.BLOCK_FOLDER + "/wool")));
        paneBlock(BlockRegistry.RGB_GLASS_PANE.get(),
                  models().withExistingParent(BlockRegistry.RGB_GLASS_PANE.getId().toString() + "_post",
                                              modLoc(ModelProvider.BLOCK_FOLDER + "/template_glass_pane_post"))
                          .texture("pane", modLoc(ModelProvider.BLOCK_FOLDER + "/glass"))
                          .texture("edge", modLoc(ModelProvider.BLOCK_FOLDER + "/glass_pane_top")),
                  models().withExistingParent(BlockRegistry.RGB_GLASS_PANE.getId().toString() + "_side",
                                              modLoc(ModelProvider.BLOCK_FOLDER + "/template_glass_pane_side"))
                          .texture("pane", modLoc(ModelProvider.BLOCK_FOLDER + "/glass"))
                          .texture("edge", modLoc(ModelProvider.BLOCK_FOLDER + "/glass_pane_top")),
                  models().withExistingParent(BlockRegistry.RGB_GLASS_PANE.getId().toString() + "_side_alt",
                                              modLoc(ModelProvider.BLOCK_FOLDER + "/template_glass_pane_side_alt"))
                          .texture("pane", modLoc(ModelProvider.BLOCK_FOLDER + "/glass"))
                          .texture("edge", modLoc(ModelProvider.BLOCK_FOLDER + "/glass_pane_top")),
                  models().withExistingParent(BlockRegistry.RGB_GLASS_PANE.getId().toString() + "_noside",
                                              modLoc(ModelProvider.BLOCK_FOLDER + "/template_glass_pane_noside"))
                          .texture("pane", modLoc(ModelProvider.BLOCK_FOLDER + "/glass")),
                  models().withExistingParent(BlockRegistry.RGB_GLASS_PANE.getId().toString() + "_noside_alt",
                                              modLoc(ModelProvider.BLOCK_FOLDER + "/template_glass_pane_noside_alt"))
                          .texture("pane", modLoc(ModelProvider.BLOCK_FOLDER + "/glass")));
        class AntiblockLoaderBuilder extends CustomLoaderBuilder<BlockModelBuilder> {
            private JsonObject baseModel;

            protected AntiblockLoaderBuilder(BlockModelBuilder parent,
                                             ExistingFileHelper existingFileHelper) {
                super(new ResourceLocation(RGBBlocks.MOD_ID, "antiblock_model"), parent, existingFileHelper);
            }

            public AntiblockLoaderBuilder baseModel(JsonObject baseModel) {
                this.baseModel = baseModel;
                models().generatedModels.remove(modLoc("block/" + BlockRegistry.RGB_ANTIBLOCK.getId().getPath() + "_base"));
                return this;
            }

            @Override
            public JsonObject toJson(JsonObject json) {
                json = super.toJson(json);
                if (baseModel != null)
                    json.add("base_model", baseModel);
                return json;
            }
        }
        simpleBlock(BlockRegistry.RGB_ANTIBLOCK.get(),
                    models().withExistingParent(BlockRegistry.RGB_ANTIBLOCK.getId().getPath(),
                                                new ResourceLocation("forge:block/default")
                            )
                            .customLoader(AntiblockLoaderBuilder::new)
                            .baseModel(models().singleTexture(BlockRegistry.RGB_ANTIBLOCK.getId().getPath() + "_base",
                                                              modLoc(ModelProvider.BLOCK_FOLDER + "/no_shade_2_layer"),
                                                              "bot",
                                                              modLoc(ModelProvider.BLOCK_FOLDER + "/white")
                                               )
                                               .texture("top", modLoc(ModelProvider.BLOCK_FOLDER + "/antiblock")).toJson())
                            .end()
        );
        getVariantBuilder(BlockRegistry.RGB_REDSTONE_LAMP.get()).forAllStates(state -> {
            return state.getValue(RedstoneLampBlock.LIT)
                   ? ConfiguredModel.builder()
                                    .modelFile(models().singleTexture(BlockRegistry.RGB_REDSTONE_LAMP.getId()
                                                                                                     .getPath() +
                                                                      "_on",
                                                                      modLoc(ModelProvider.BLOCK_FOLDER +
                                                                             "/block"),
                                                                      "all",
                                                                      modLoc(ModelProvider.BLOCK_FOLDER +
                                                                             "/redstone_lamp_on")))
                                    .build()
                   : ConfiguredModel.builder()
                                    .modelFile(models().singleTexture(BlockRegistry.RGB_REDSTONE_LAMP.getId().getPath(),
                                                                      modLoc(ModelProvider.BLOCK_FOLDER + "/block"),
                                                                      "all",
                                                                      modLoc(ModelProvider.BLOCK_FOLDER +
                                                                             "/redstone_lamp")))
                                    .build();
        });

        blocks(BlockRegistry.RGB_CONCRETE.get());
        blocks(BlockRegistry.RGB_CONCRETE_POWDER.get());
        blocks(BlockRegistry.RGB_WOOL.get());
        blocks(BlockRegistry.RGB_PLANKS.get());
        blocks(BlockRegistry.RGB_TERRACOTTA.get());
        blocks(BlockRegistry.RGB_GLASS.get());
        blocks(BlockRegistry.RGB_GLOWSTONE.get());
        blocks(BlockRegistry.RGB_PRISMARINE.get());
        blocks(BlockRegistry.RGB_PRISMARINE_BRICKS.get());
        blocks(BlockRegistry.RGB_DARK_PRISMARINE.get());
        blocks(BlockRegistry.RGB_SEA_LANTERN.get());

        slabBlocks(BlockRegistry.RGB_CONCRETE_SLAB.get());
        slabBlocks(BlockRegistry.RGB_WOOL_SLAB.get());
        slabBlocks(BlockRegistry.RGB_PLANKS_SLAB.get());
        slabBlocks(BlockRegistry.RGB_TERRACOTTA_SLAB.get());
        slabBlocks(BlockRegistry.RGB_GLASS_SLAB.get());
        slabBlocks(BlockRegistry.RGB_PRISMARINE_SLAB.get());
        slabBlocks(BlockRegistry.RGB_PRISMARINE_BRICK_SLAB.get());
        slabBlocks(BlockRegistry.RGB_DARK_PRISMARINE_SLAB.get());

        stairBlocks(BlockRegistry.RGB_CONCRETE_STAIRS.get());
        stairBlocks(BlockRegistry.RGB_WOOL_STAIRS.get());
        stairBlocks(BlockRegistry.RGB_PLANKS_STAIRS.get());
        stairBlocks(BlockRegistry.RGB_TERRACOTTA_STAIRS.get());
        stairBlocks(BlockRegistry.RGB_GLASS_STAIRS.get());
        stairBlocks(BlockRegistry.RGB_PRISMARINE_STAIRS.get());
        stairBlocks(BlockRegistry.RGB_PRISMARINE_BRICK_STAIRS.get());
        stairBlocks(BlockRegistry.RGB_DARK_PRISMARINE_STAIRS.get());

        RegistryHandler.BLOCKS.getEntries()
                              .stream()
                              .filter((block) -> block.get() != BlockRegistry.RGB_GLASS_PANE.get())
                              .forEach((block) -> blockItems(block.get()));
    }

    private void blocks(Block block) {
        String path = block.getRegistryName().getPath();
        String loc = ModelProvider.BLOCK_FOLDER + "/" + path;
        simpleBlock(block,
                    models().singleTexture(path, modLoc(ModelProvider.BLOCK_FOLDER + "/block"), "all", modLoc(loc)));
    }

    private void slabBlocks(Block block) {
        String path = block.getRegistryName().getPath();
        String loc = ModelProvider.BLOCK_FOLDER + "/" + path.replace("_slab", "");
        ModelFile slabBottom = models().withExistingParent(path, modLoc(ModelProvider.BLOCK_FOLDER + "/slab"))
                                       .texture("all", loc);
        ModelFile slabTop = models().withExistingParent(path + "_top", modLoc(ModelProvider.BLOCK_FOLDER + "/slab_top"))
                                    .texture("all", loc);
        ModelFile slabDouble = models().getExistingFile(modLoc(loc));
        slabBlock((SlabBlock) block, slabBottom, slabTop, slabDouble);
        ConfiguredModel.builder().modelFile(slabBottom).build();
    }

    private void stairBlocks(Block block) {
        String path = block.getRegistryName().getPath();
        String loc = ModelProvider.BLOCK_FOLDER + "/" + path.replace("_stairs", "");
        ModelFile stairs = models().withExistingParent(path, modLoc(ModelProvider.BLOCK_FOLDER + "/stairs"))
                                   .texture("all", loc);
        ModelFile stairsInner = models().withExistingParent(path + "_inner",
                                                            modLoc(ModelProvider.BLOCK_FOLDER + "/inner_stairs"))
                                        .texture("all", loc);
        ModelFile stairsOuter = models().withExistingParent(path + "_outer",
                                                            modLoc(ModelProvider.BLOCK_FOLDER + "/outer_stairs"))
                                        .texture("all", loc);
        stairsBlock((StairBlock) block, stairs, stairsInner, stairsOuter);
        ConfiguredModel.builder().modelFile(stairs).build();
    }

    private void blockItems(Block block) {
        String path = block.getRegistryName().getPath();
        simpleBlockItem(block, models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/" + path)));
    }
}
