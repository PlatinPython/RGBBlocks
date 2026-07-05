package platinpython.rgbblocks.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGatherer {
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(ModLanguageProvider::new);
        event.createProvider(ModModelProvider::new);

        event.createProvider(ModRecipeProvider.Runner::new);
        event.createProvider(ModLootTableProvider::new);
        event.createProvider(ModBlockTagsProvider::new);
    }
}
