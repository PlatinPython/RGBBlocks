package platinpython.rgbblocks.util.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.network.packets.PaintbucketSyncPKT;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(RGBBlocks.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void register() {
		int index = 0;
		INSTANCE.registerMessage(index++, PaintbucketSyncPKT.class, PaintbucketSyncPKT::encode, PaintbucketSyncPKT::decode,
				PaintbucketSyncPKT.Handler::handle);
	}
	
	public static void sendToServer(Object message) {
		INSTANCE.sendToServer(message);
	}
}
