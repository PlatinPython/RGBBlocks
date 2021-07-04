package platinpython.rgbblocks.client.colorhandlers;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class RGBBlockColor implements IBlockColor {
	public int getColor(BlockState blockState, IBlockDisplayReader blockDisplayReader, BlockPos blockPos, int tintindex) {
		if (blockDisplayReader == null) {
			return -1;
		}
		TileEntity tileEntity = blockDisplayReader.getBlockEntity(blockPos);
		if (tileEntity == null) {
			tileEntity = blockDisplayReader.getBlockEntity(blockPos.below());
			if (tileEntity == null) {
				return -1;
			}
		}
		if (tileEntity instanceof RGBTileEntity) {
			return ((RGBTileEntity) tileEntity).getColor();
		} else {
			return -1;
		}
	}
}
