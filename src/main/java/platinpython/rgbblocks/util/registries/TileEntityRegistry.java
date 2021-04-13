package platinpython.rgbblocks.util.registries;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.tileentity.RGBLampTileEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.RegistryHandler;

public class TileEntityRegistry {
	public static final RegistryObject<TileEntityType<RGBTileEntity>> RGB = RegistryHandler.TILE_ENTITY_TYPES
			.register("rgb", () -> TileEntityType.Builder
					.create(RGBTileEntity::new, BlockRegistry.nonGlowingBlocks.toArray(new Block[0])).build(null));
	public static final RegistryObject<TileEntityType<RGBLampTileEntity>> RGB_LAMP = RegistryHandler.TILE_ENTITY_TYPES
			.register("rgb_lamp", () -> TileEntityType.Builder.create(RGBLampTileEntity::new).build(null));

	public static void register() {
	}
}
