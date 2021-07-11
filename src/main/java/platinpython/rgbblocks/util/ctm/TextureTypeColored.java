package platinpython.rgbblocks.util.ctm;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import team.chisel.ctm.api.texture.ICTMTexture;
import team.chisel.ctm.api.texture.TextureType;
import team.chisel.ctm.api.util.TextureInfo;
import team.chisel.ctm.client.texture.ctx.TextureContextCTM;
import team.chisel.ctm.client.texture.render.TextureCTM;
import team.chisel.ctm.client.texture.type.TextureTypeCTM;
import team.chisel.ctm.client.util.CTMLogic;

@TextureType(RGBBlocks.MOD_ID + ":colored")
public class TextureTypeColored extends TextureTypeCTM {
	@Override
	public ICTMTexture<? extends TextureTypeCTM> makeTexture(TextureInfo info) {
		return new TextureCTM<TextureTypeCTM>(this, info);
	}

	@Override
	public TextureContextCTM getBlockRenderContext(BlockState state, IBlockReader world, BlockPos pos, ICTMTexture<?> tex) {
		return new TextureContextCTM(state, world, pos, (TextureCTM<?>) tex) {
			@Override
			protected CTMLogic createCTM(BlockState state) {
				return new CTMLogicColored();
			}
		};
	}

	public static class CTMLogicColored extends CTMLogic {
		@Override
		public boolean isConnected(IBlockReader world, BlockPos current, BlockPos connection, Direction dir, BlockState state) {
			if(world.getBlockState(current).getBlock() != world.getBlockState(connection).getBlock()) {
				return false;
			}
			TileEntity currentTileEntity = world.getBlockEntity(current);
			TileEntity connectionTileEntity = world.getBlockEntity(connection);

			if (currentTileEntity instanceof RGBTileEntity && connectionTileEntity instanceof RGBTileEntity) {
				if (((RGBTileEntity) currentTileEntity).getColor() == ((RGBTileEntity) connectionTileEntity).getColor()) {
					BlockPos obscuringPos = connection.relative(dir);
					BlockState obscuring = getConnectionState(world, obscuringPos, dir, current);
					boolean b = true;
					return (b &= !stateComparator(state, obscuring, dir));
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
}
