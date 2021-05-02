package platinpython.rgbblocks.tileentity;

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

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("color", color);
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.color = compound.getInt("color");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getPos(), -1, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.read(packet.getNbtCompound());
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
	}
}
