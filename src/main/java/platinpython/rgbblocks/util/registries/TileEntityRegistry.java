package platinpython.rgbblocks.util.registries;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.RegistryHandler;

public class TileEntityRegistry {
	public static final RegistryObject<TileEntityType<RGBTileEntity>> RGB = RegistryHandler.TILE_ENTITY_TYPES.register("rgb", () -> TileEntityType.Builder.of(RGBTileEntity::new, RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));

	public static void register() {
	}
}
