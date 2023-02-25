package platinpython.rgbblocks.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.registries.BlockRegistry;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, RGBBlocks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.IMPERMEABLE)
            .add(BlockRegistry.RGB_GLASS.get(), BlockRegistry.RGB_GLASS_SLAB.get(),
                 BlockRegistry.RGB_GLASS_STAIRS.get()
            );

        this.tag(BlockTags.MINEABLE_WITH_AXE)
            .add(BlockRegistry.RGB_PLANKS.get(), BlockRegistry.RGB_PLANKS_SLAB.get(),
                 BlockRegistry.RGB_PLANKS_STAIRS.get()
            );
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(BlockRegistry.RGB_CONCRETE.get(), BlockRegistry.RGB_CONCRETE_SLAB.get(),
                 BlockRegistry.RGB_CONCRETE_STAIRS.get(), BlockRegistry.RGB_TERRACOTTA.get(),
                 BlockRegistry.RGB_TERRACOTTA_SLAB.get(), BlockRegistry.RGB_TERRACOTTA_STAIRS.get(),
                 BlockRegistry.RGB_ANTIBLOCK.get(), BlockRegistry.RGB_PRISMARINE.get(),
                 BlockRegistry.RGB_PRISMARINE_SLAB.get(), BlockRegistry.RGB_PRISMARINE_STAIRS.get(),
                 BlockRegistry.RGB_PRISMARINE_BRICKS.get(), BlockRegistry.RGB_PRISMARINE_BRICK_SLAB.get(),
                 BlockRegistry.RGB_PRISMARINE_BRICK_STAIRS.get(), BlockRegistry.RGB_DARK_PRISMARINE.get(),
                 BlockRegistry.RGB_DARK_PRISMARINE_SLAB.get(), BlockRegistry.RGB_DARK_PRISMARINE_STAIRS.get()
            );
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.RGB_CONCRETE_POWDER.get());
    }
}
