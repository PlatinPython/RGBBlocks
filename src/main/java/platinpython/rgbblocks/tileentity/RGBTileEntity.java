package platinpython.rgbblocks.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public class RGBTileEntity extends TileEntity {
	public int color;

	public RGBTileEntity(final TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public RGBTileEntity() {
		this(TileEntityRegistry.RGB.get());
	}
	
	public int getColor() {
		return color;
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("color", color);
		return super.save(compound);
	}

	@Override
	public void load(BlockState blockState, CompoundNBT compound) {
		super.load(blockState, compound);
		this.color = compound.getInt("color");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getBlockPos(), -1, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.load(null, packet.getTag());
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}
}
