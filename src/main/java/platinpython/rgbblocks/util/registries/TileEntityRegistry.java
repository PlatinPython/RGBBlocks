package platinpython.rgbblocks.util.registries;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.tileentity.RGBLampTileEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.RegistryHandler;

public class TileEntityRegistry {
	public static final RegistryObject<TileEntityType<RGBTileEntity>> RGB = RegistryHandler.TILE_ENTITY_TYPES.register(
			"rgb",
			() -> TileEntityType.Builder
					.create(RGBTileEntity::new, BlockRegistry.RGB_FLAT.get(), BlockRegistry.RGB_GLASS_FLAT.get())
					.build(null));
	public static final RegistryObject<TileEntityType<RGBLampTileEntity>> RGB_LAMP = RegistryHandler.TILE_ENTITY_TYPES
			.register("rgb_lamp", () -> TileEntityType.Builder.create(RGBLampTileEntity::new,
					BlockRegistry.RGB_LAMP_FLAT.get(), BlockRegistry.RGB_LAMP_GLASS_FLAT.get()).build(null));

	public static void register() {
	}
}
