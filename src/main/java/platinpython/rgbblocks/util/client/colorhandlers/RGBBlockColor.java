package platinpython.rgbblocks.util.client.colorhandlers;

import java.awt.Color;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import platinpython.rgbblocks.tileentity.RGBLampTileEntity;
import platinpython.rgbblocks.tileentity.RGBTileEntity;

@OnlyIn(Dist.CLIENT)
public class RGBBlockColor implements IBlockColor {
	public int getColor(BlockState blockState, ILightReader lightReader, BlockPos blockPos, int tintindex) {
		@SuppressWarnings("resource")
		TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(blockPos);
		if (tileEntity instanceof RGBTileEntity) {
			CompoundNBT compound = tileEntity.getUpdateTag();
			Color color = new Color(compound.getInt("red"), compound.getInt("green"), compound.getInt("blue"));
			return color.getRGB();
		} else if (tileEntity instanceof RGBLampTileEntity) {
			CompoundNBT compound = tileEntity.getUpdateTag();
			Color color = Color.BLACK;
			if (compound.getBoolean("isOn")) {
				color = new Color(compound.getInt("red"), compound.getInt("green"), compound.getInt("blue"));
			} else {
				color = new Color((int) (compound.getInt("red") / 3), (int) (compound.getInt("green") / 3),
						(int) (compound.getInt("blue") / 3));
			}
			return color.getRGB();
		} else {
			return Color.WHITE.getRGB();
		}
	}
}
