package platinpython.rgbblocks.data;

import net.minecraft.block.Block;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
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
		simpleBlock(BlockRegistry.RGB_ANTIBLOCK.get(), models().singleTexture(BlockRegistry.RGB_ANTIBLOCK.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/no_shade"), "all", modLoc(ModelProvider.BLOCK_FOLDER + "/antiblock")));
		simpleBlock(BlockRegistry.RGB_CARPET.get(), models().singleTexture(BlockRegistry.RGB_CARPET.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/thin_block"), "all", modLoc(ModelProvider.BLOCK_FOLDER + "/wool")));
		getVariantBuilder(BlockRegistry.RGB_REDSTONE_LAMP.get()).forAllStates(state -> {
			return state.getValue(RedstoneLampBlock.LIT) ? ConfiguredModel.builder().modelFile(models().singleTexture(BlockRegistry.RGB_REDSTONE_LAMP.getId().getPath() + "_on", modLoc(ModelProvider.BLOCK_FOLDER + "/block"), "all", modLoc(ModelProvider.BLOCK_FOLDER + "/redstone_lamp_on"))).build() : ConfiguredModel.builder().modelFile(models().singleTexture(BlockRegistry.RGB_REDSTONE_LAMP.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/block"), "all", modLoc(ModelProvider.BLOCK_FOLDER + "/redstone_lamp"))).build();
		});

		blocks(BlockRegistry.RGB_CONCRETE.get());
		blocks(BlockRegistry.RGB_CONCRETE_POWDER.get());
		blocks(BlockRegistry.RGB_GLASS.get());
		blocks(BlockRegistry.RGB_GLOWSTONE.get());
		blocks(BlockRegistry.RGB_PLANKS.get());
		blocks(BlockRegistry.RGB_TERRACOTTA.get());
		blocks(BlockRegistry.RGB_WOOL.get());

		slabBlocks(BlockRegistry.RGB_CONCRETE_SLAB.get());
		slabBlocks(BlockRegistry.RGB_GLASS_SLAB.get());
		slabBlocks(BlockRegistry.RGB_PLANKS_SLAB.get());
		slabBlocks(BlockRegistry.RGB_TERRACOTTA_SLAB.get());
		slabBlocks(BlockRegistry.RGB_WOOL_SLAB.get());

		stairBlocks(BlockRegistry.RGB_CONCRETE_STAIRS.get());
		stairBlocks(BlockRegistry.RGB_GLASS_STAIRS.get());
		stairBlocks(BlockRegistry.RGB_PLANKS_STAIRS.get());
		stairBlocks(BlockRegistry.RGB_TERRACOTTA_STAIRS.get());
		stairBlocks(BlockRegistry.RGB_WOOL_STAIRS.get());

		RegistryHandler.BLOCKS.getEntries().forEach((block) -> blockItems(block.get()));

	}

	private void blocks(Block block) {
		String path = block.getRegistryName().getPath();
		String loc = ModelProvider.BLOCK_FOLDER + "/" + path;
		simpleBlock(block, models().singleTexture(path, modLoc(ModelProvider.BLOCK_FOLDER + "/block"), "all", modLoc(loc)));
	}

	private void slabBlocks(Block block) {
		String path = block.getRegistryName().getPath();
		String loc = ModelProvider.BLOCK_FOLDER + "/" + path.replace("_slab", "");
		ModelFile slabBottom = models().withExistingParent(path, modLoc(ModelProvider.BLOCK_FOLDER + "/slab")).texture("all", loc);
		ModelFile slabTop = models().withExistingParent(path + "_top", modLoc(ModelProvider.BLOCK_FOLDER + "/slab_top")).texture("all", loc);
		ModelFile slabDouble = models().getExistingFile(modLoc(loc));
		slabBlock((SlabBlock) block, slabBottom, slabTop, slabDouble);
		ConfiguredModel.builder().modelFile(slabBottom).build();
	}

	private void stairBlocks(Block block) {
		String path = block.getRegistryName().getPath();
		String loc = ModelProvider.BLOCK_FOLDER + "/" + path.replace("_stairs", "");
		ModelFile stairs = models().withExistingParent(path, modLoc(ModelProvider.BLOCK_FOLDER + "/stairs")).texture("all", loc);
		ModelFile stairsInner = models().withExistingParent(path + "_inner", modLoc(ModelProvider.BLOCK_FOLDER + "/inner_stairs")).texture("all", loc);
		ModelFile stairsOuter = models().withExistingParent(path + "_outer", modLoc(ModelProvider.BLOCK_FOLDER + "/outer_stairs")).texture("all", loc);
		stairsBlock((StairsBlock) block, stairs, stairsInner, stairsOuter);
		ConfiguredModel.builder().modelFile(stairs).build();
	}

	private void blockItems(Block block) {
		String path = block.getRegistryName().getPath();
		simpleBlockItem(block, models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/" + path)));
	}
}
