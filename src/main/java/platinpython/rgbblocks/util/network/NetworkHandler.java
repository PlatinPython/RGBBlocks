package platinpython.rgbblocks.util.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.util.network.packets.PaintBucketSyncPKT;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(RGBBlocks.MOD_ID).versioned(PROTOCOL_VERSION);
        registrar.play(
            PaintBucketSyncPKT.ID, PaintBucketSyncPKT::new,
            handler -> handler.server(PaintBucketSyncPKT.Handler::handle)
        );
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.SERVER.noArg().send(message);
    }
}
