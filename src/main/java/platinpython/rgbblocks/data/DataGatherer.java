package platinpython.rgbblocks.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import platinpython.rgbblocks.RGBBlocks;

public class DataGatherer {
    private static final String PATH_PREFIX = "textures/block";
    private static final String PATH_SUFFIX = ".png";

    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        addVirtualPackContents(existingFileHelper);

        generator.addProvider(event.includeClient(), new ModLanguageProvider(output));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModRecipeProvider(output));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(output));
        generator.addProvider(
            event.includeServer(), new ModBlockTagsProvider(output, event.getLookupProvider(), existingFileHelper)
        );
    }

    private static void addVirtualPackContents(ExistingFileHelper existingFileHelper) {
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "concrete"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "concrete_powder"), PackType.CLIENT_RESOURCES, PATH_SUFFIX,
            PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "wool"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "planks"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "terracotta"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "glass"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "glass_pane_top"), PackType.CLIENT_RESOURCES, PATH_SUFFIX,
            PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "glowstone"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "redstone_lamp"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "redstone_lamp_on"), PackType.CLIENT_RESOURCES, PATH_SUFFIX,
            PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "prismarine"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "prismarine_bricks"), PackType.CLIENT_RESOURCES, PATH_SUFFIX,
            PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "dark_prismarine"), PackType.CLIENT_RESOURCES, PATH_SUFFIX,
            PATH_PREFIX
        );
        existingFileHelper.trackGenerated(
            new ResourceLocation(RGBBlocks.MOD_ID, "sea_lantern"), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX
        );
    }
}
