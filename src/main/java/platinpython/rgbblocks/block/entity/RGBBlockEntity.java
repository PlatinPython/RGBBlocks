package platinpython.rgbblocks.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.BlockEntityRegistry;

public class RGBBlockEntity extends BlockEntity {
    private int color;
    private MapColor mapColor = MapColor.NONE;

    public RGBBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.RGB.get(), pos, state);
    }

    public void setColor(int color) {
        this.color = new Color(color).getRGB();
        this.mapColor = Color.getNearestMapColor(this.color);
        setChanged();
    }

    public int getColor() {
        return color;
    }

    public MapColor getMapColor() {
        return mapColor;
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putInt("color", getColor());
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        setColor(compound.getInt("color"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("color", color);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        setColor(tag.getInt("color"));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet.getTag() != null) {
            setColor(packet.getTag().getInt("color"));
        }
        if (this.level != null) {
            this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }
    }
}
