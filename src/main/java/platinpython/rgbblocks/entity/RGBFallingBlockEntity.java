package platinpython.rgbblocks.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
        return this.color;
    }

    @Override
    public @Nullable ItemEntity spawnAtLocation(ServerLevel level, ItemStack stack, Vec3 offset) {
        stack.set(DataComponentRegistry.COLOR, this.color);
        return super.spawnAtLocation(level, stack, offset);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("color", this.color);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.color = input.getIntOr("color", -1);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.color);
        buffer.writeVarInt(Block.getId(this.blockState));
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        this.color = buffer.readInt();
        this.blockState = Block.stateById(buffer.readVarInt());
    }
}
