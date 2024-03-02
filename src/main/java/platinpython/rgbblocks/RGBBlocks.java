package platinpython.rgbblocks;

import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import platinpython.rgbblocks.data.DataGatherer;
import platinpython.rgbblocks.dispenser.DispensePaintBucketBehaviour;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.compat.framedblocks.RGBBlocksFramedBlocks;
import platinpython.rgbblocks.util.compat.top.TOPMain;
import platinpython.rgbblocks.util.network.NetworkHandler;
import platinpython.rgbblocks.util.registries.ItemRegistry;

@Mod(RGBBlocks.MOD_ID)
public class RGBBlocks {
    public static final String MOD_ID = "rgbblocks";

    public static final Logger LOGGER = LogManager.getLogger();

    public RGBBlocks(IEventBus bus) {
        bus.addListener(this::setup);
        bus.addListener(this::enqueueIMC);
        bus.addListener(DataGatherer::onGatherData);
        bus.addListener(NetworkHandler::register);

        RegistryHandler.register(bus);

        if (ModList.get().isLoaded("framedblocks")) {
            RGBBlocksFramedBlocks.register(bus);
        }
    }

    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(
            () -> DispenserBlock.registerBehavior(ItemRegistry.PAINT_BUCKET.get(), new DispensePaintBucketBehaviour())
        );
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPMain::new);
        }
    }
}
