package platinpython.rgbblocks.entity;

import java.lang.reflect.Field;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import platinpython.rgbblocks.util.registries.EntityRegistry;

public class RGBFallingBlockEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {
	private int color;
	public static final Field blockState = ObfuscationReflectionHelper.findField(FallingBlockEntity.class, "field_175132_d");

	public RGBFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(EntityRegistry.RGB_FALLING_BLOCK.get(), world);
	}

	public RGBFallingBlockEntity(World world, double x, double y, double z, int color) {
		this(EntityRegistry.RGB_FALLING_BLOCK.get(), world);
		this.blocksBuilding = true;
		this.setPos(x, y + (double) ((1.0F - this.getBbHeight()) / 2.0F), z);
		this.color = color;
	}
	
	public int getColor() {
		return color;
	}
	
	@Override
	public ItemEntity spawnAtLocation(ItemStack stack, float offset) {
		stack.getOrCreateTag().putInt("color", color);
		return super.spawnAtLocation(stack, offset);
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("color", color);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		color = compound.getInt("color");
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeInt(color);
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		color = additionalData.readInt();
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
