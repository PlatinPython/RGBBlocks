package platinpython.rgbblocks.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fmllegacy.RegistryObject;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(Pair.of(Blocks::new, LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((name, table) -> LootTables.validate(validationtracker, name, table));
    }

    private class Blocks extends BlockLoot {
        @Override
        protected void addTables() {
            HashMap<Block, Function<Block, LootTable.Builder>> map = new HashMap<>();

            map.put(BlockRegistry.RGB_ANTIBLOCK.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_CARPET.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_CONCRETE.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_CONCRETE_POWDER.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_CONCRETE_SLAB.get(), (block) -> createSlabItemTable(block));
            map.put(BlockRegistry.RGB_CONCRETE_STAIRS.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_GLASS.get(), (block) -> createSilkTouchOnlyTable(block));
            map.put(BlockRegistry.RGB_GLASS_SLAB.get(), (block) -> createSilkTouchOnlySlabItemTable(block));
            map.put(BlockRegistry.RGB_GLASS_STAIRS.get(), (block) -> createSilkTouchOnlyTable(block));
            map.put(BlockRegistry.RGB_GLOWSTONE.get(),
                    (block) -> Blocks.createSilkTouchDispatchTable(block,
                                                                   applyExplosionDecay(block,
                                                                                       LootItem.lootTableItem(Items.GLOWSTONE_DUST)
                                                                                               .apply(SetItemCountFunction.setCount(
                                                                                                       UniformGenerator.between(
                                                                                                               2.0F,
                                                                                                               4.0F)))
                                                                                               .apply(ApplyBonusCount.addUniformBonusCount(
                                                                                                       Enchantments.BLOCK_FORTUNE))
                                                                                               .apply(LimitCount.limitCount(
                                                                                                       IntRange.range(1,
                                                                                                                      4))))));
            map.put(BlockRegistry.RGB_PLANKS.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_PLANKS_SLAB.get(), (block) -> createSlabItemTable(block));
            map.put(BlockRegistry.RGB_PLANKS_STAIRS.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_REDSTONE_LAMP.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_TERRACOTTA.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_TERRACOTTA_SLAB.get(), (block) -> createSlabItemTable(block));
            map.put(BlockRegistry.RGB_TERRACOTTA_STAIRS.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_WOOL.get(), (block) -> createSingleItemTable(block));
            map.put(BlockRegistry.RGB_WOOL_SLAB.get(), (block) -> createSlabItemTable(block));
            map.put(BlockRegistry.RGB_WOOL_STAIRS.get(), (block) -> createSingleItemTable(block));

            map.forEach((block, function) -> add(block,
                                                 block == BlockRegistry.RGB_GLOWSTONE.get() ? applyConditionalNbtCopy(
                                                         function.apply(block)) : applyNbtCopy(function.apply(block))));
        }

        private LootTable.Builder createSilkTouchOnlySlabItemTable(Block block) {
            return LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                              .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                                                                               .hasEnchantment(new EnchantmentPredicate(
                                                                                                       Enchantments.SILK_TOUCH,
                                                                                                       MinMaxBounds.Ints.atLeast(
                                                                                                               1)))))
                                              .setRolls(ConstantValue.exactly(1))
                                              .add(applyExplosionDecay(block,
                                                                       LootItem.lootTableItem(block)
                                                                               .apply(SetItemCountFunction.setCount(
                                                                                                                  ConstantValue.exactly(2))
                                                                                                          .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(
                                                                                                                                                           block)
                                                                                                                                                   .setProperties(
                                                                                                                                                           StatePropertiesPredicate.Builder.properties()
                                                                                                                                                                                           .hasProperty(
                                                                                                                                                                                                   SlabBlock.TYPE,
                                                                                                                                                                                                   SlabType.DOUBLE)))))));
        }

        private LootTable.Builder applyNbtCopy(LootTable.Builder table) {
            RGBBlocks.LOGGER.debug(table.toString());
            return table.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("color", "color"));
        }

        private LootTable.Builder applyConditionalNbtCopy(LootTable.Builder table) {
            return table.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                              .copy("color", "color")
                                              .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                                                                               .hasEnchantment(new EnchantmentPredicate(
                                                                                                       Enchantments.SILK_TOUCH,
                                                                                                       MinMaxBounds.Ints.atLeast(
                                                                                                               1))))));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        }
    }
}
