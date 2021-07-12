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

import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.IntClamper;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.CopyNbt.Source;
import net.minecraft.loot.functions.LimitCount;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.BlockRegistry;

public class ModLootTableProvider extends LootTableProvider {
	public ModLootTableProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> getTables() {
		return ImmutableList.of(Pair.of(Blocks::new, LootParameterSets.BLOCK));
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
		map.forEach((name, table) -> LootTableManager.validate(validationtracker, name, table));
	}

	private class Blocks extends BlockLootTables {
		@Override
		protected void addTables() {
			HashMap<Block, Function<Block, LootTable.Builder>> map = new HashMap<>();
			map.put(BlockRegistry.RGB_ANTIBLOCK.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_CARPET.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_CONCRETE.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_CONCRETE_POWDER.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_CONCRETE_SLAB.get(), BlockLootTables::createSlabItemTable);
			map.put(BlockRegistry.RGB_CONCRETE_STAIRS.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_GLASS.get(), BlockLootTables::createSilkTouchOnlyTable);
			map.put(BlockRegistry.RGB_GLASS_SLAB.get(), (block) -> createSilkTouchOnlySlabItemTable(block));
			map.put(BlockRegistry.RGB_GLASS_STAIRS.get(), BlockLootTables::createSilkTouchOnlyTable);
			map.put(BlockRegistry.RGB_GLOWSTONE.get(), (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, ItemLootEntry.lootTableItem(Items.GLOWSTONE_DUST).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntClamper.clamp(1, 4))))));
			map.put(BlockRegistry.RGB_PLANKS.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_PLANKS_SLAB.get(), BlockLootTables::createSlabItemTable);
			map.put(BlockRegistry.RGB_PLANKS_STAIRS.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_REDSTONE_LAMP.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_TERRACOTTA.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_TERRACOTTA_SLAB.get(), BlockLootTables::createSlabItemTable);
			map.put(BlockRegistry.RGB_TERRACOTTA_STAIRS.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_WOOL.get(), BlockLootTables::createSingleItemTable);
			map.put(BlockRegistry.RGB_WOOL_SLAB.get(), BlockLootTables::createSlabItemTable);
			map.put(BlockRegistry.RGB_WOOL_STAIRS.get(), BlockLootTables::createSingleItemTable);
			map.forEach((block, function) -> add(block, applyNbtCopy(function.apply(block))));
		}

		private LootTable.Builder createSilkTouchOnlySlabItemTable(Block block) {
			return LootTable.lootTable().withPool(LootPool.lootPool().when(MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))).setRolls(ConstantRange.exactly(1)).add(applyExplosionDecay(block, ItemLootEntry.lootTableItem(block).apply(SetCount.setCount(ConstantRange.exactly(2)).when(BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)))))));
		}

		private LootTable.Builder applyNbtCopy(LootTable.Builder table) {
			return table.apply(CopyNbt.copyData(Source.BLOCK_ENTITY).copy("color", "color"));
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
		}
	}
}
