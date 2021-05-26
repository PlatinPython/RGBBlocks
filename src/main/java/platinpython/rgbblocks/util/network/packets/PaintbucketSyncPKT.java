package platinpython.rgbblocks.util.network.packets;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import platinpython.rgbblocks.item.PaintbucketItem;

public class PaintbucketSyncPKT {
	private final int color;
	private final boolean isRGBSelected;
	
	public PaintbucketSyncPKT(int color, boolean isRGBSelected) {
		this.color = color;
		this.isRGBSelected = isRGBSelected;
	}
	
	public static void encode(PaintbucketSyncPKT message, PacketBuffer buffer) {
		buffer.writeInt(message.color);
		buffer.writeBoolean(message.isRGBSelected);
	}

	public static PaintbucketSyncPKT decode(PacketBuffer buffer) {
		return new PaintbucketSyncPKT(buffer.readInt(), buffer.readBoolean());
	}
	
	public static class Handler{
		public static void handle(PaintbucketSyncPKT message, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				ItemStack stack = context.get().getSender().getMainHandItem();
				if(stack.getItem() instanceof PaintbucketItem) {
					stack.getOrCreateTag().putInt("color", message.color);
					stack.getOrCreateTag().putBoolean("isRGBSelected", message.isRGBSelected);
				}
			});
			context.get().setPacketHandled(true);
		}
	}
}
