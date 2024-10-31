package platinpython.rgbblocks.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.pack.RGBBlocksPack;

import java.util.concurrent.CompletableFuture;

public class DataGatherer {
    private static final String PATH_PREFIX = "textures/block";
    private static final String PATH_SUFFIX = ".png";

    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        addVirtualPackContents(existingFileHelper);

        generator.addProvider(event.includeClient(), new ModLanguageProvider(output));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(output, lookupProvider));
        generator
            .addProvider(event.includeServer(), new ModBlockTagsProvider(output, lookupProvider, existingFileHelper));
    }

    private static void addVirtualPackContents(ExistingFileHelper existingFileHelper) {
        RGBBlocksPack.MOD_TO_VANILLA_MAP.keySet()
            .forEach(
                name -> existingFileHelper.trackGenerated(
                    ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, name), PackType.CLIENT_RESOURCES,
                    PATH_SUFFIX, PATH_PREFIX
                )
            );
    }
}
