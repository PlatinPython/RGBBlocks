package platinpython.rgbblocks.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants.BlockFlags;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public class RGBTileEntity extends TileEntity {
	private int color;

	public RGBTileEntity(final TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public RGBTileEntity() {
		this(TileEntityRegistry.RGB.get());
	}

	public void setColor(int color) {
		this.color = color;
		setChanged();
	}

	public int getColor() {
		return color;
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("color", getColor());
		return super.save(compound);
	}

	@Override
	public void load(BlockState blockState, CompoundNBT compound) {
		super.load(blockState, compound);
		setColor(compound.getInt("color"));
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.putInt("color", color);
		return tag;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		setColor(tag.getInt("color"));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("color", getColor());
		return new SUpdateTileEntityPacket(this.getBlockPos(), -1, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		setColor(packet.getTag().getInt("color"));
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), BlockFlags.DEFAULT_AND_RERENDER);
	}
}
