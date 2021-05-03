package platinpython.rgbblocks.util.network.packets;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import platinpython.rgbblocks.item.PaintbucketItem;

public class PaintbucketSyncPKT {
	private final int color;
	
	public PaintbucketSyncPKT(int color) {
		this.color = color;
	}
	
	public static void encode(PaintbucketSyncPKT message, PacketBuffer buffer) {
		buffer.writeInt(message.color);
	}

	public static PaintbucketSyncPKT decode(PacketBuffer buffer) {
		return new PaintbucketSyncPKT(buffer.readInt());
	}
	
	public static class Handler{
		public static void handle(PaintbucketSyncPKT message, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				ItemStack stack = context.get().getSender().getMainHandItem();
				if(stack.getItem() instanceof PaintbucketItem) {
					stack.getOrCreateTag().putInt("color", message.color);
				}
			});
			context.get().setPacketHandled(true);
		}
	}
}
