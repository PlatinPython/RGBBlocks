package platinpython.rgbblocks.util.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

public record PaintBucketSyncPayload(int color, boolean isRGBSelected) implements CustomPacketPayload {
    public static final Type<PaintBucketSyncPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "paint_bucket_sync"));
    public static final StreamCodec<ByteBuf, PaintBucketSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, PaintBucketSyncPayload::color, ByteBufCodecs.BOOL, PaintBucketSyncPayload::isRGBSelected,
        PaintBucketSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<PaintBucketSyncPayload> {
        public void handle(PaintBucketSyncPayload message, IPayloadContext context) {
            ItemStack stack = context.player().getMainHandItem();
            if (stack.getItem() instanceof PaintBucketItem) {
                stack.set(DataComponentRegistry.COLOR, message.color);
                stack.set(DataComponentRegistry.RGB_SELECTED, message.isRGBSelected);
            }
        }
    }
}
