package platinpython.rgbblocks.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public class RGBLampTileEntity extends TileEntity {
	public int red, green, blue;
	public boolean isOn;

	public RGBLampTileEntity(final TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public RGBLampTileEntity() {
		this(TileEntityRegistry.RGB_LAMP.get());
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("red", Math.min(Math.max(this.red, 0), 255));
		compound.putInt("green", Math.min(Math.max(this.green, 0), 255));
		compound.putInt("blue", Math.min(Math.max(this.blue, 0), 255));
		compound.putBoolean("isOn", this.isOn);
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.red = compound.getInt("red");
		this.green = compound.getInt("green");
		this.blue = compound.getInt("blue");
		this.isOn = compound.getBoolean("isOn");
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
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), -1);
	}

	public void lampToggle() {
		this.isOn = !isOn;
		world.getLightManager().checkBlock(pos);
	}
}
