package platinpython.rgbblocks.util.client.colorhandlers;

import java.awt.Color;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import platinpython.rgbblocks.tileentity.RGBLampTileEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

public class RGBBlockColor implements IBlockColor {
	public int getColor(BlockState blockState, IBlockDisplayReader blockDisplayReader, BlockPos blockPos, int tintindex) {
		TileEntity tileEntity = blockDisplayReader.getBlockEntity(blockPos);
		if (tileEntity.getUpdateTag().contains("color")) {
			if (tileEntity instanceof RGBTileEntity) {
				return tileEntity.getUpdateTag().getInt("color");
			} else if (tileEntity instanceof RGBLampTileEntity) {
				CompoundNBT compound = tileEntity.getUpdateTag();
				Color color = Color.BLACK;
				if (compound.getBoolean("isOn")) {
					color = new Color(compound.getInt("color"));
				} else {
					color = new Color(compound.getInt("color")).darker();
				}
				return color.getRGB();
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
}
