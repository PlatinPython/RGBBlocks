package platinpython.rgbblocks.util.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import platinpython.rgbblocks.util.network.packets.PaintBucketSyncPayload;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(
            PaintBucketSyncPayload.TYPE, PaintBucketSyncPayload.STREAM_CODEC, new PaintBucketSyncPayload.Handler()
        );
    }
}
