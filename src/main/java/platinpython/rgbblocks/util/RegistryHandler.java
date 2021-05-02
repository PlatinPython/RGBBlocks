package platinpython.rgbblocks.util;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.client.colorhandlers.PaintbucketItemColor;
import platinpython.rgbblocks.util.client.colorhandlers.RGBBlockColor;
import platinpython.rgbblocks.util.client.colorhandlers.RGBBlockItemColor;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.FluidRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public class RegistryHandler {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RGBBlocks.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			RGBBlocks.MOD_ID);
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister
			.create(ForgeRegistries.TILE_ENTITIES, RGBBlocks.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,
			RGBBlocks.MOD_ID);

	public static void register() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
		TILE_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

		ItemRegistry.register();
		BlockRegistry.register();
		FluidRegistry.register();
		TileEntityRegistry.register();
	}

	@SubscribeEvent
	public void registerRGBColors(ColorHandlerEvent.Item event) {
		event.getBlockColors().register(new RGBBlockColor(),
				RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new));
		event.getItemColors().register(new RGBBlockItemColor(),
				RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new));
		
		event.getItemColors().register(new PaintbucketItemColor(), ItemRegistry.BUCKET_OF_PAINT.get());
	}
}
