package platinpython.rgbblocks.util.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import platinpython.rgbblocks.item.PaintBucketItem;

import java.util.function.Supplier;

public class PaintBucketSyncPKT {
    private final int color;
    private final boolean isRGBSelected;

    public PaintBucketSyncPKT(int color, boolean isRGBSelected) {
        this.color = color;
        this.isRGBSelected = isRGBSelected;
    }

    public static void encode(PaintBucketSyncPKT message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.color);
        buffer.writeBoolean(message.isRGBSelected);
    }

    public static PaintBucketSyncPKT decode(FriendlyByteBuf buffer) {
        return new PaintBucketSyncPKT(buffer.readInt(), buffer.readBoolean());
    }

    public static class Handler {
        public static void handle(PaintBucketSyncPKT message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (player == null) {
                    return;
                }
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof PaintBucketItem) {
                    stack.getOrCreateTag().putInt("color", message.color);
                    stack.getOrCreateTag().putBoolean("isRGBSelected", message.isRGBSelected);
                }
            });
            context.get().setPacketHandled(true);
        }
    }
}
