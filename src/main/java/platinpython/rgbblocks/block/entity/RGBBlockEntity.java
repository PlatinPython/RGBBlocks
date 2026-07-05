package platinpython.rgbblocks.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.color = components.getOrDefault(DataComponentRegistry.COLOR, -1);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentRegistry.COLOR, this.color);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("color", this.getColor());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.setColor(input.getIntOr("color", -1));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput input) {
        super.onDataPacket(net, input);
        if (this.level != null) {
            this.level
                .sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }
    }
}
