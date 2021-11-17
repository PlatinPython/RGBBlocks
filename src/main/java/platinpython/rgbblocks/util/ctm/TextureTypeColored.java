package platinpython.rgbblocks.util.ctm;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
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
    public TextureContextCTM getBlockRenderContext(BlockState state, BlockGetter world, BlockPos pos,
                                                   ICTMTexture<?> tex) {
        return new TextureContextCTM(state, world, pos, (TextureCTM<?>) tex) {
            @Override
            protected CTMLogic createCTM(BlockState state) {
                return new CTMLogicColored();
            }
        };
    }

    public static class CTMLogicColored extends CTMLogic {
        @Override
        public boolean isConnected(BlockGetter world, BlockPos current, BlockPos connection, Direction dir,
                                   BlockState state) {
            if (world.getBlockState(current).getBlock() != world.getBlockState(connection).getBlock()) {
                return false;
            }
            BlockEntity currentTileEntity = world.getBlockEntity(current);
            BlockEntity connectionTileEntity = world.getBlockEntity(connection);

            if (currentTileEntity instanceof RGBTileEntity && connectionTileEntity instanceof RGBTileEntity) {
                if (((RGBTileEntity) currentTileEntity).getColor() ==
                    ((RGBTileEntity) connectionTileEntity).getColor()) {
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
