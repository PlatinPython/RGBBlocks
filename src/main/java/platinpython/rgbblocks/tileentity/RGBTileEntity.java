package platinpython.rgbblocks.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Constants.BlockFlags;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public class RGBTileEntity extends BlockEntity {
    private int color;

    public RGBTileEntity(BlockPos pos, BlockState state) {
        super(TileEntityRegistry.RGB.get(), pos, state);
    }

    public void setColor(int color) {
        this.color = new Color(color).getRGB();
        setChanged();
    }

    public int getColor() {
        return color;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putInt("color", getColor());
        return super.save(compound);
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
        CompoundTag compound = new CompoundTag();
        compound.putInt("color", getColor());
        return new ClientboundBlockEntityDataPacket(this.getBlockPos(), -1, compound);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        setColor(packet.getTag().getInt("color"));
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), BlockFlags.DEFAULT_AND_RERENDER);
    }
}
