package platinpython.rgbblocks.util.network.packets;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import platinpython.rgbblocks.item.PaintBucketItem;

public class PaintBucketSyncPKT {
    private final int color;
    private final boolean isRGBSelected;

    public PaintBucketSyncPKT(int color, boolean isRGBSelected) {
        this.color = color;
        this.isRGBSelected = isRGBSelected;
    }

    public static void encode(PaintBucketSyncPKT message, PacketBuffer buffer) {
        buffer.writeInt(message.color);
        buffer.writeBoolean(message.isRGBSelected);
    }

    public static PaintBucketSyncPKT decode(PacketBuffer buffer) {
        return new PaintBucketSyncPKT(buffer.readInt(), buffer.readBoolean());
    }

    public static class Handler {
        public static void handle(PaintBucketSyncPKT message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                ItemStack stack = context.get().getSender().getMainHandItem();
                if (stack.getItem() instanceof PaintBucketItem) {
                    stack.getOrCreateTag().putInt("color", message.color);
                    stack.getOrCreateTag().putBoolean("isRGBSelected", message.isRGBSelected);
                }
            });
            context.get().setPacketHandled(true);
        }
    }
}
