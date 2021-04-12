package platinpython.rgbblocks.block;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public interface IGlassBlock {
	public static float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos,
			BlockPos beaconPos) {
		RGBTileEntity tileEntity = (RGBTileEntity) world.getTileEntity(pos);
		CompoundNBT compound = tileEntity.getUpdateTag();
		return new float[] { (float) compound.getInt("red") / 255.0F, (float) compound.getInt("green") / 255.0F,
				(float) compound.getInt("blue") / 255.0F };
	}
}
