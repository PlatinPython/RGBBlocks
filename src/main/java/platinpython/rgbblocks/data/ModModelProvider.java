package platinpython.rgbblocks.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.client.colorhandlers.RGBItemTintSource;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, RGBBlocks.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        BlockFamilies.getAllFamilies()
            .filter(BlockFamily::shouldGenerateModel)
            .forEach(blockFamily -> this.family(blockModels, blockFamily.getBaseBlock()).generateFor(blockFamily));
        blockModels.createColoredBlockWithRandomRotations(TexturedModels.CUBE, BlockRegistry.RGB_CONCRETE_POWDER.get());
        this.createCarpetBlock(blockModels, BlockRegistry.RGB_WOOL.get(), BlockRegistry.RGB_CARPET.get());
        this.createGlassPane(blockModels, BlockRegistry.RGB_GLASS.get(), BlockRegistry.RGB_GLASS_PANE.get());
        this.createRedstoneLamp(blockModels);

        itemModels.generateItemWithTintedOverlay(ItemRegistry.PAINT_BUCKET.get(), "_color", new RGBItemTintSource());
    }

    private BlockFamilyProvider family(BlockModelGenerators blockModels, Block block) {
        TexturedModel model = TexturedModels.CUBE.get(block);
        return new BlockFamilyProvider(blockModels, model.getMapping()).fullBlock(block, model.getTemplate());
    }

    private void createCarpetBlock(BlockModelGenerators blockModels, Block block, Block carpet) {
        MultiVariant model =
            BlockModelGenerators.plainVariant(TexturedModels.CARPET.get(block).create(carpet, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(carpet, model));
    }

    private void createGlassPane(BlockModelGenerators blockModels, Block block, Block pane) {
        TextureMapping paneMapping = TextureMapping.pane(block, pane).forceAllTranslucent();
        MultiVariant post = BlockModelGenerators
            .plainVariant(ModelTemplates.STAINED_GLASS_PANE_POST.create(pane, paneMapping, blockModels.modelOutput));
        MultiVariant side = BlockModelGenerators
            .plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE.create(pane, paneMapping, blockModels.modelOutput));
        MultiVariant sideAlt = BlockModelGenerators.plainVariant(
            ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(pane, paneMapping, blockModels.modelOutput)
        );
        MultiVariant noSide = BlockModelGenerators
            .plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(pane, paneMapping, blockModels.modelOutput));
        MultiVariant noSideAlt = BlockModelGenerators.plainVariant(
            ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(pane, paneMapping, blockModels.modelOutput)
        );
        blockModels.registerSimpleTintedItemModel(
            pane, blockModels.createFlatItemModelWithBlockTexture(pane.asItem(), block), new RGBItemTintSource()
        );
        blockModels.blockStateOutput.accept(
            MultiPartGenerator.multiPart(pane)
                .with(post)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), side)
                .with(
                    BlockModelGenerators.condition().term(BlockStateProperties.EAST, true),
                    side.with(BlockModelGenerators.Y_ROT_90)
                )
                .with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), sideAlt)
                .with(
                    BlockModelGenerators.condition().term(BlockStateProperties.WEST, true),
                    sideAlt.with(BlockModelGenerators.Y_ROT_90)
                )
                .with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false), noSide)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, false), noSideAlt)
                .with(
                    BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, false),
                    noSideAlt.with(BlockModelGenerators.Y_ROT_90)
                )
                .with(
                    BlockModelGenerators.condition().term(BlockStateProperties.WEST, false),
                    noSide.with(BlockModelGenerators.Y_ROT_270)
                )
        );
    }

    private void createRedstoneLamp(BlockModelGenerators blockModels) {
        MultiVariant off = BlockModelGenerators
            .plainVariant(TexturedModels.CUBE.create(BlockRegistry.RGB_REDSTONE_LAMP.get(), blockModels.modelOutput));
        MultiVariant on = BlockModelGenerators.plainVariant(
            blockModels.createSuffixedVariant(
                BlockRegistry.RGB_REDSTONE_LAMP.get(), "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube
            )
        );
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(BlockRegistry.RGB_REDSTONE_LAMP.get())
                .with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, on, off))
        );
    }

    private static class BlockFamilyProvider {
        private static final Map<BlockFamily.Variant, BiConsumer<BlockFamilyProvider, Block>> SHAPE_CONSUMERS =
            ImmutableMap.<BlockFamily.Variant, BiConsumer<BlockFamilyProvider, Block>>builder()
                .put(BlockFamily.Variant.SLAB, BlockFamilyProvider::slab)
                .put(BlockFamily.Variant.STAIRS, BlockFamilyProvider::stairs)
                .build();
        private final BlockModelGenerators blockModels;
        private final TextureMapping mapping;
        private final Map<ModelTemplate, Identifier> models;
        private @Nullable Variant fullBlock;

        public BlockFamilyProvider(BlockModelGenerators blockModels, TextureMapping mapping) {
            this.blockModels = blockModels;
            this.models = new HashMap<>();
            this.mapping = mapping;
        }

        public BlockFamilyProvider fullBlock(Block block, ModelTemplate template) {
            this.fullBlock =
                BlockModelGenerators.plainModel(template.create(block, this.mapping, this.blockModels.modelOutput));
            this.blockModels.blockStateOutput
                .accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.variant(this.fullBlock)));
            return this;
        }

        public void slab(Block slab) {
            if (this.fullBlock == null) {
                throw new IllegalStateException("Full block not generated yet");
            } else {
                Identifier bottom = this.getOrCreateModel(ModelTemplates.SLAB_BOTTOM, slab);
                MultiVariant top =
                    BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.SLAB_TOP, slab));
                this.blockModels.blockStateOutput.accept(
                    BlockModelGenerators.createSlab(
                        slab, BlockModelGenerators.plainVariant(bottom), top,
                        BlockModelGenerators.variant(this.fullBlock)
                    )
                );
                this.blockModels.registerSimpleTintedItemModel(slab, bottom, new RGBItemTintSource());
            }
        }

        public void stairs(Block stairs) {
            MultiVariant inner =
                BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.STAIRS_INNER, stairs));
            Identifier straight = this.getOrCreateModel(ModelTemplates.STAIRS_STRAIGHT, stairs);
            MultiVariant outer =
                BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.STAIRS_OUTER, stairs));
            this.blockModels.blockStateOutput.accept(
                BlockModelGenerators.createStairs(stairs, inner, BlockModelGenerators.plainVariant(straight), outer)
            );
            this.blockModels.registerSimpleTintedItemModel(stairs, straight, new RGBItemTintSource());
        }

        public Identifier getOrCreateModel(ModelTemplate modelTemplate, Block block) {
            return this.models.computeIfAbsent(
                modelTemplate, template -> template.create(block, this.mapping, this.blockModels.modelOutput)
            );
        }

        public void generateFor(BlockFamily family) {
            family.getVariants().forEach((variant, result) -> {
                boolean modelAlreadyRegisteredAsAnotherFamilyBase =
                    net.minecraft.data.BlockFamilies.getAllFamilies().anyMatch(b -> b.getBaseBlock() == result);
                if (!modelAlreadyRegisteredAsAnotherFamilyBase) {
                    BiConsumer<BlockFamilyProvider, Block> consumer = SHAPE_CONSUMERS.get(variant);
                    if (consumer != null) {
                        consumer.accept(this, result);
                    }
                }
            });
        }
    }

    private static class ModelTemplates {
        public static final ModelTemplate CUBE_ALL = create("cube_all", TextureSlot.ALL);
        public static final ModelTemplate SLAB_BOTTOM =
            create("slab", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        public static final ModelTemplate SLAB_TOP =
            create("slab_top", "_top", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        public static final ModelTemplate STAIRS_STRAIGHT =
            create("stairs", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        public static final ModelTemplate STAIRS_INNER =
            create("inner_stairs", "_inner", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        public static final ModelTemplate STAIRS_OUTER =
            create("outer_stairs", "_outer", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        public static final ModelTemplate CARPET = create("thin_block", TextureSlot.WOOL);
        public static final ModelTemplate STAINED_GLASS_PANE_NOSIDE =
            create("template_glass_pane_noside", "_noside", TextureSlot.PANE);
        public static final ModelTemplate STAINED_GLASS_PANE_NOSIDE_ALT =
            create("template_glass_pane_noside_alt", "_noside_alt", TextureSlot.PANE);
        public static final ModelTemplate STAINED_GLASS_PANE_POST =
            create("template_glass_pane_post", "_post", TextureSlot.PANE, TextureSlot.EDGE);
        public static final ModelTemplate STAINED_GLASS_PANE_SIDE =
            create("template_glass_pane_side", "_side", TextureSlot.PANE, TextureSlot.EDGE);
        public static final ModelTemplate STAINED_GLASS_PANE_SIDE_ALT =
            create("template_glass_pane_side_alt", "_side_alt", TextureSlot.PANE, TextureSlot.EDGE);

        public static ModelTemplate create(String id, TextureSlot... slots) {
            return new ModelTemplate(
                Optional.of(Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, id).withPrefix("block/")),
                Optional.empty(), slots
            );
        }

        public static ModelTemplate create(String id, String suffix, TextureSlot... slots) {
            return new ModelTemplate(
                Optional.of(Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, id).withPrefix("block/")),
                Optional.of(suffix), slots
            );
        }
    }

    private static class TexturedModels {
        public static final TexturedModel.Provider CUBE =
            TexturedModel.createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL);
        public static final TexturedModel.Provider CARPET =
            TexturedModel.createDefault(TextureMapping::wool, ModelTemplates.CARPET);
    }
}
