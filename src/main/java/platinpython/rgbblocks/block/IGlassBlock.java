package platinpython.rgbblocks.block;

import java.awt.Color;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public interface IGlassBlock {
	public static float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos,
			BlockPos beaconPos) {
		CompoundNBT compound = world.getBlockEntity(pos).getUpdateTag();
		if (compound.contains("color")) {
			return new Color(compound.getInt("color")).getRGBColorComponents(null);
		} else {
			return Color.WHITE.getRGBColorComponents(null);
		}
	}
}
