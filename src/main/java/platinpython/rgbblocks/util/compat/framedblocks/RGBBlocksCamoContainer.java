package platinpython.rgbblocks.util.compat.framedblocks;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerClientHandler;
import io.github.xfacthd.framedblocks.api.camo.TriggerRegistrar;
import io.github.xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

public class RGBBlocksCamoContainer extends AbstractBlockCamoContainer<RGBBlocksCamoContainer> {
    final int color;
    final MapColor mapColor;

    protected RGBBlocksCamoContainer(BlockState state, int color) {
        super(state);
        this.color = color;
        this.mapColor = Color.getNearestMapColor(this.color);
    }

    @Override
    public MapColor getMapColor(BlockGetter level, BlockPos pos) {
        return mapColor;
    }

    @Override
    public @Nullable Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return this.color;
    }

    @Override
    public CamoContainerClientHandler<BlockCamoContent, RGBBlocksCamoContainer> getClientHandler() {
        return RGBBlocksCamoContainerClientHandler.INSTANCE;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.content.getState(), this.color);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RGBBlocksCamoContainer that = (RGBBlocksCamoContainer) o;
        return color == that.color && mapColor == that.mapColor && content.equals(that.content);
    }

    @Override
    public AbstractBlockCamoContainerFactory<RGBBlocksCamoContainer> getFactory() {
        return RGBBlocksFramedBlocks.RGBBLOCKS_CONTAINER_FACTORY.get();
    }

    public static class Factory extends AbstractBlockCamoContainerFactory<RGBBlocksCamoContainer> {
        private static final MapCodec<RGBBlocksCamoContainer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                .group(
                    BlockState.CODEC.fieldOf("state").forGetter(RGBBlocksCamoContainer::getState),
                    Codec.INT.fieldOf("color").forGetter(container -> container.color)
                )
                .apply(instance, RGBBlocksCamoContainer::new)
        );
        private static final StreamCodec<ByteBuf, RGBBlocksCamoContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), RGBBlocksCamoContainer::getState, ByteBufCodecs.INT,
            container -> container.color, RGBBlocksCamoContainer::new
        );

        @Override
        protected RGBBlocksCamoContainer createContainer(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            ItemAccess itemAccess
        ) {
            return new RGBBlocksCamoContainer(
                state, itemAccess.getResource().getOrDefault(DataComponentRegistry.COLOR, -1)
            );
        }

        @Override
        protected RGBBlocksCamoContainer copyContainerWithState(RGBBlocksCamoContainer container, BlockState state) {
            return new RGBBlocksCamoContainer(state, container.color);
        }

        @Override
        protected ItemStack createItemStack(
            Level level,
            BlockPos pos,
            Player player,
            ItemAccess itemAccess,
            RGBBlocksCamoContainer container
        ) {
            return this.dropCamo(container);
        }

        @Override
        protected boolean isValidBlock(BlockState state, BlockGetter level, BlockPos pos, @Nullable Player player) {
            return RGBBlocksFramedBlocks.VALID_BLOCKS.get().contains(state.getBlock());
        }

        @Override
        protected void writeToNetwork(ValueOutput output, RGBBlocksCamoContainer container) {
            output.putInt("state", Block.getId(container.getState()));
            output.putInt("color", container.color);
        }

        @Override
        protected RGBBlocksCamoContainer readFromNetwork(ValueInput input) {
            return new RGBBlocksCamoContainer(
                Block.stateById(input.getIntOr("state", -1)), input.getIntOr("color", -1)
            );
        }

        @Override
        public boolean canTriviallyConvertToItemStack() {
            return false;
        }

        @Override
        public ItemStack dropCamo(RGBBlocksCamoContainer container) {
            ItemStack stack = new ItemStack(container.getState().getBlock());
            stack.set(DataComponentRegistry.COLOR, container.color);
            return stack;
        }

        @Override
        public RGBBlocksCamoContainer handleInteraction(
            Level level,
            BlockPos pos,
            Player player,
            RGBBlocksCamoContainer camo,
            ItemStack stack,
            InteractionHand hand
        ) {
            if (!player.isCreative() && stack.getOrDefault(DataComponentRegistry.COLOR, -1) != camo.color) {
                if (stack.getDamageValue() == stack.getMaxDamage() - 1) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                } else {
                    player.getItemInHand(hand).hurtAndBreak(1, player, hand);
                }
            }
            return new RGBBlocksCamoContainer(camo.getState(), stack.getOrDefault(DataComponentRegistry.COLOR, -1));
        }

        @Override
        public MapCodec<RGBBlocksCamoContainer> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RGBBlocksCamoContainer> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public void registerTriggerItems(TriggerRegistrar registrar) {
            RGBBlocksFramedBlocks.VALID_BLOCKS.get()
                .stream()
                .map(ItemLike::asItem)
                .forEach(registrar::registerApplicationItem);
            registrar.registerRemovalItem(FramedConstants.Objects.FRAMED_HAMMER.value());
        }
    }
}
