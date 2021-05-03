package platinpython.rgbblocks.util.network.packets;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import platinpython.rgbblocks.item.PaintbucketItem;

public class PaintbucketSyncPKT {
	private final CompoundNBT compound;
	
	public PaintbucketSyncPKT(CompoundNBT compound) {
		this.compound = compound;
	}
	
	public static void encode(PaintbucketSyncPKT message, PacketBuffer buffer) {
		buffer.writeNbt(message.compound);
	}

	public static PaintbucketSyncPKT decode(PacketBuffer buffer) {
		return new PaintbucketSyncPKT(buffer.readNbt());
	}
	
	public static class Handler{
		public static void handle(PaintbucketSyncPKT message, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				ItemStack stack = context.get().getSender().getMainHandItem();
				if(stack.getItem() instanceof PaintbucketItem) {
					stack.setTag(message.compound);
				}
			});
			context.get().setPacketHandled(true);
		}
	}
}
