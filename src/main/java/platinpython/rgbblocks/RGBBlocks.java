package platinpython.rgbblocks;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import platinpython.rgbblocks.data.DataGatherer;
import platinpython.rgbblocks.dispenser.DispensePaintbucketBehaviour;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.compat.framedblocks.RGBBlocksFramedBlocks;
import platinpython.rgbblocks.util.compat.top.TOPMain;
import platinpython.rgbblocks.util.network.PacketHandler;
import platinpython.rgbblocks.util.registries.ItemRegistry;

@Mod(RGBBlocks.MOD_ID)
public class RGBBlocks {
    public static final String MOD_ID = "rgbblocks";

    public static final Logger LOGGER = LogManager.getLogger();

    public RGBBlocks() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGatherer::onGatherData);

        RegistryHandler.register();

        MinecraftForge.EVENT_BUS.register(this);

        if (ModList.get().isLoaded("framedblocks")) {
            RGBBlocksFramedBlocks.register();
        }
    }

    public void setup(final FMLCommonSetupEvent event) {
        PacketHandler.register();

        event.enqueueWork(
            () -> DispenserBlock.registerBehavior(ItemRegistry.PAINT_BUCKET.get(), new DispensePaintbucketBehaviour())
        );
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPMain::new);
        }
    }

    @SubscribeEvent
    public void replaceMappings(MissingMappingsEvent event) {
        for (
            MissingMappingsEvent.Mapping<Item> mapping : event.getAllMappings(ForgeRegistries.ITEMS.getRegistryKey())
        ) {
            if (mapping.getKey().toString().equals("rgbblocks:bucket_of_paint")) {
                mapping.remap(ItemRegistry.PAINT_BUCKET.get());
            }
        }
    }
}
