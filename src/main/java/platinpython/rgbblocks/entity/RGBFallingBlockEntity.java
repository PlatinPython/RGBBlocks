package platinpython.rgbblocks.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;
import platinpython.rgbblocks.util.registries.EntityRegistry;

public class RGBFallingBlockEntity extends FallingBlockEntity implements IEntityWithComplexSpawn {
    private int color;

    public RGBFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }

    public RGBFallingBlockEntity(Level level, double x, double y, double z, BlockState state, int color) {
        this(EntityRegistry.RGB_FALLING_BLOCK.get(), level);
        this.blockState = state;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public @Nullable ItemEntity spawnAtLocation(ItemStack stack, float offset) {
        stack.set(DataComponentRegistry.COLOR, this.color);
        return super.spawnAtLocation(stack, offset);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("color", color);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        color = compound.getInt("color");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(color);
        buffer.writeVarInt(Block.getId(blockState));
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        color = buffer.readInt();
        blockState = Block.stateById(buffer.readVarInt());
    }
}
