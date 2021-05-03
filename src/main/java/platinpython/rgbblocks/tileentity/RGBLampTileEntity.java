package platinpython.rgbblocks.tileentity;

import java.awt.Color;

import net.minecraft.block.BlockState;
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
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("color", new Color(Math.min(Math.max(this.red, 0), 255), Math.min(Math.max(this.green, 0), 255),
				Math.min(Math.max(this.blue, 0), 255)).getRGB());
		compound.putBoolean("isOn", this.isOn);
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		Color color = new Color(compound.getInt("color"));
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.isOn = compound.getBoolean("isOn");
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

	public void lampToggle() {
		this.isOn = !isOn;
		level.getLightEngine().checkBlock(worldPosition);
	}
}
