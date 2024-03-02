package platinpython.rgbblocks.util.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.item.PaintBucketItem;

public record PaintBucketSyncPKT(int color, boolean isRGBSelected) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RGBBlocks.MOD_ID, "paint_bucket_sync");

    public PaintBucketSyncPKT(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(color);
        buffer.writeBoolean(isRGBSelected);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
    public static class Handler {
        public static void handle(PaintBucketSyncPKT message, PlayPayloadContext context) {
            context.workHandler()
                .submitAsync(
                    () -> context.player()
                        .map(LivingEntity::getMainHandItem)
                        .filter(stack -> stack.getItem() instanceof PaintBucketItem)
                        .ifPresent(stack -> {
                            stack.getOrCreateTag().putInt("color", message.color);
                            stack.getOrCreateTag().putBoolean("isRGBSelected", message.isRGBSelected);
                        })
                );
        }
    }
}
