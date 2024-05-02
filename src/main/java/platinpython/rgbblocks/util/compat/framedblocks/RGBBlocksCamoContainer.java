package platinpython.rgbblocks.util.compat.framedblocks;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.RegistryHandler;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;
import xfacthd.framedblocks.api.camo.TriggerRegistrar;
import xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainer;
import xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainerFactory;
import xfacthd.framedblocks.api.util.CamoMessageVerbosity;
import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.api.util.Utils;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RGBBlocksCamoContainer extends AbstractBlockCamoContainer<RGBBlocksCamoContainer> {
    int color;
    MapColor mapColor;

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
    public float[] getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return new Color(this.color).getRGBColorComponents();
    }

    @Override
    public int getTintColor(BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        return color;
    }

    @Override
    public boolean equals(Object o) {
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
    public int hashCode() {
        return Objects.hashCode(this.content.getState(), this.color);
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
        @SuppressWarnings("deprecation")
        private static final StreamCodec<ByteBuf, RGBBlocksCamoContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), RGBBlocksCamoContainer::getState, ByteBufCodecs.INT,
            container -> container.color, RGBBlocksCamoContainer::new
        );
        public static final Component MSG_BLOCK_ENTITY = Utils.translate("msg", "camo.block_entity");
        public static final Component MSG_NON_SOLID = Utils.translate("msg", "camo.non_solid");

        @Override
        protected RGBBlocksCamoContainer createContainer(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            ItemStack stack
        ) {
            return new RGBBlocksCamoContainer(state, stack.getOrDefault(DataComponentRegistry.COLOR, -1));
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
            ItemStack stack,
            RGBBlocksCamoContainer container
        ) {
            return this.dropCamo(container);
        }

        @Override
        protected boolean isValidBlock(BlockState state, BlockGetter level, BlockPos pos, @Nullable Player player) {
            if (state.is(Utils.BLOCK_BLACKLIST)) {
                displayValidationMessage(player, MSG_BLACKLISTED, CamoMessageVerbosity.DEFAULT);
                return false;
            }
            if (state.hasBlockEntity() && !ConfigView.Server.INSTANCE.allowBlockEntities()
                && !state.is(Utils.BE_WHITELIST)) {
                displayValidationMessage(player, MSG_BLOCK_ENTITY, CamoMessageVerbosity.DEFAULT);
                return false;
            }
            if (!state.isSolidRender(level, pos) && !state.is(Utils.FRAMEABLE)) {
                displayValidationMessage(player, MSG_NON_SOLID, CamoMessageVerbosity.DETAILED);
                return false;
            }
            return RegistryHandler.BLOCKS.getEntries()
                .stream()
                .map(Supplier::get)
                .anyMatch(Predicate.isEqual(state.getBlock()));
        }

        @Override
        protected void writeToNetwork(CompoundTag tag, RGBBlocksCamoContainer container) {
            tag.putInt("state", Block.getId(container.getState()));
            tag.putInt("color", container.color);
        }

        @Override
        protected RGBBlocksCamoContainer readFromNetwork(CompoundTag tag) {
            return new RGBBlocksCamoContainer(Block.stateById(tag.getInt("state")), tag.getInt("color"));
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
        public MapCodec<RGBBlocksCamoContainer> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RGBBlocksCamoContainer> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public void registerTriggerItems(TriggerRegistrar registrar) {
            registrar.registerApplicationPredicate(stack -> stack.getItem() instanceof RGBBlockItem);
            registrar.registerRemovalItem(Utils.FRAMED_HAMMER.value());
        }
    }
}
