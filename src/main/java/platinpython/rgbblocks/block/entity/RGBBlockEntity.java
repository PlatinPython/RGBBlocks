package platinpython.rgbblocks.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.BlockEntityRegistry;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

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
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.color = componentInput.getOrDefault(DataComponentRegistry.COLOR, -1);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentRegistry.COLOR, this.color);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("color", getColor());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        setColor(tag.getInt("color"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putInt("color", color);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        setColor(tag.getInt("color"));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        setColor(packet.getTag().getInt("color"));
        if (this.level != null) {
            this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }
    }
}
