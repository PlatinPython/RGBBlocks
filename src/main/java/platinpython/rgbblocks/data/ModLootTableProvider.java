package platinpython.rgbblocks.data;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(
            output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)),
            lookupProvider
        );
    }

    private static class Blocks extends BlockLootSubProvider {
        private final HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup =
            this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        protected Blocks(HolderLookup.Provider lookupProvider) {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        public void generate() {
            HashMap<Block, Function<Block, LootTable.Builder>> map = new HashMap<>();

            map.put(BlockRegistry.RGB_CONCRETE.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_CONCRETE_SLAB.get(), this::createSlabItemTable);
            map.put(BlockRegistry.RGB_CONCRETE_STAIRS.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_CONCRETE_POWDER.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_WOOL.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_WOOL_SLAB.get(), this::createSlabItemTable);
            map.put(BlockRegistry.RGB_WOOL_STAIRS.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_CARPET.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_PLANKS.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_PLANKS_SLAB.get(), this::createSlabItemTable);
            map.put(BlockRegistry.RGB_PLANKS_STAIRS.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_TERRACOTTA.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_TERRACOTTA_SLAB.get(), this::createSlabItemTable);
            map.put(BlockRegistry.RGB_TERRACOTTA_STAIRS.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_GLASS.get(), this::createSilkTouchOnlyTable);
            map.put(BlockRegistry.RGB_GLASS_SLAB.get(), this::createSilkTouchOnlySlabItemTable);
            map.put(BlockRegistry.RGB_GLASS_STAIRS.get(), this::createSilkTouchOnlyTable);
            map.put(BlockRegistry.RGB_GLASS_PANE.get(), this::createSilkTouchOnlyTable);
            map.put(BlockRegistry.RGB_ANTIBLOCK.get(), this::createSingleItemTable);
            map.put(
                BlockRegistry.RGB_GLOWSTONE.get(),
                block -> this.createSilkTouchDispatchTable(
                    block,
                    this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(Items.GLOWSTONE_DUST)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                            .apply(
                                ApplyBonusCount
                                    .addUniformBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))
                            )
                            .apply(LimitCount.limitCount(IntRange.range(1, 4)))
                    )
                )
            );
            map.put(BlockRegistry.RGB_REDSTONE_LAMP.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_PRISMARINE.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_PRISMARINE_SLAB.get(), this::createSlabItemTable);
            map.put(BlockRegistry.RGB_PRISMARINE_STAIRS.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_PRISMARINE_BRICKS.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_PRISMARINE_BRICK_SLAB.get(), this::createSlabItemTable);
            map.put(BlockRegistry.RGB_PRISMARINE_BRICK_STAIRS.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_DARK_PRISMARINE.get(), this::createSingleItemTable);
            map.put(BlockRegistry.RGB_DARK_PRISMARINE_SLAB.get(), this::createSlabItemTable);
            map.put(BlockRegistry.RGB_DARK_PRISMARINE_STAIRS.get(), this::createSingleItemTable);
            map.put(
                BlockRegistry.RGB_SEA_LANTERN.get(),
                block -> this.createSilkTouchDispatchTable(
                    block,
                    this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                            .apply(
                                ApplyBonusCount
                                    .addUniformBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))
                            )
                            .apply(LimitCount.limitCount(IntRange.range(1, 5)))
                    )
                )
            );

            map.forEach(
                (block, function) -> add(
                    block,
                    block == BlockRegistry.RGB_GLOWSTONE.get() || block == BlockRegistry.RGB_SEA_LANTERN.get()
                        ? applyConditionalNbtCopy(function.apply(block))
                        : applyNbtCopy(function.apply(block))
                )
            );
        }

        private LootTable.Builder createSilkTouchOnlySlabItemTable(Block block) {
            return LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .when(this.hasSilkTouch())
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            this.applyExplosionDecay(
                                block,
                                LootItem.lootTableItem(block)
                                    .apply(
                                        SetItemCountFunction.setCount(ConstantValue.exactly(2))
                                            .when(
                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                    .setProperties(
                                                        StatePropertiesPredicate.Builder.properties()
                                                            .hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)
                                                    )
                                            )
                                    )
                            )
                        )
                );
        }

        private LootTable.Builder applyNbtCopy(LootTable.Builder table) {
            return table.apply(
                CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                    .include(DataComponentRegistry.COLOR.get())
            );
        }

        private LootTable.Builder applyConditionalNbtCopy(LootTable.Builder table) {
            return table.apply(
                CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                    .include(DataComponentRegistry.COLOR.get())
                    .when(this.hasSilkTouch())
            );
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RegistryHandler.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
        }
    }
}
