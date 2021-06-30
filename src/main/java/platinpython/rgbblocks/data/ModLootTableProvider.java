package platinpython.rgbblocks.data;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.CopyNbt.Source;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.util.RegistryHandler;

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
			RegistryHandler.BLOCKS.getEntries()
					.forEach((block) -> add(block.get(), createDropSelfWithNbt(block.get())));
		}

		private LootTable.Builder createDropSelfWithNbt(IItemProvider block) {
			return createSingleItemTable(block).apply(CopyNbt.copyData(Source.BLOCK_ENTITY).copy("color", "color"));
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
		}
	}
}
