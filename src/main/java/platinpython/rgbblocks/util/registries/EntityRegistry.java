package platinpython.rgbblocks.util.registries;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;
import platinpython.rgbblocks.util.RegistryHandler;

public class EntityRegistry {
	public static final RegistryObject<EntityType<RGBFallingBlockEntity>> RGB_FALLING_BLOCK = RegistryHandler.ENTITY_TYPES.register("rgb_falling_block", () -> EntityType.Builder.<RGBFallingBlockEntity>of(RGBFallingBlockEntity::new, EntityClassification.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20).build(new ResourceLocation(RGBBlocks.MOD_ID, "rgb_falling_block").toString()));

	public static void register() {
	}
}
