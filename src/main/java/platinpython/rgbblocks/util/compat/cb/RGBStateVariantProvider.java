package platinpython.rgbblocks.util.compat.cb;

import com.communi.suggestu.scena.core.fluid.FluidInformation;
import mod.chiselsandbits.api.variant.state.IStateVariant;
import mod.chiselsandbits.api.variant.state.IStateVariantProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.item.RGBBlockItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class RGBStateVariantProvider implements IStateVariantProvider {
    private final Supplier<Block> block;

    public RGBStateVariantProvider(Supplier<Block> block) {
        this.block = block;
    }

    @Override
    public Optional<IStateVariant> getStateVariant(BlockState state, Optional<BlockEntity> blockEntity) {
        if (!state.is(block.get())) {
            return Optional.empty();
        }

        return blockEntity.filter(RGBBlockEntity.class::isInstance)
            .map(RGBBlockEntity.class::cast)
            .map(be -> new RGBStateVariant(be.getColor()));
    }

    @Override
    public Optional<IStateVariant> getStateVariant(FluidState state) {
        return Optional.empty();
    }

    @Override
    public Optional<IStateVariant> getStateVariant(BlockState state, ItemStack stack) {
        if (!(stack.getItem() instanceof RGBBlockItem item) || item.getBlock() != block.get()) {
            return Optional.empty();
        }
        return Optional.of(new RGBStateVariant(stack.getOrCreateTag().getInt("color")));
    }

    @Override
    public Optional<IStateVariant> getStateVariant(FluidInformation fluidInformation) {
        return Optional.empty();
    }

    @Override
    public Collection<IStateVariant> getAllDefaultVariants(BlockState state) {
        return List.of();
    }

    @Override
    public CompoundTag serializeNBT(IStateVariant stateVariant) {
        CompoundTag tag = new CompoundTag();
        if (stateVariant instanceof RGBStateVariant rgbStateVariant) {
            tag.putInt("color", rgbStateVariant.color());
        }
        return tag;
    }

    @Override
    public IStateVariant deserializeNBT(CompoundTag tag) {
        if (!tag.contains("color")) {
            return RGBStateVariant.WHITE;
        }
        return new RGBStateVariant(tag.getInt("color"));
    }

    @Override
    public void serializeInto(FriendlyByteBuf buffer, IStateVariant stateVariant) {
        if (!(stateVariant instanceof RGBStateVariant rgbStateVariant)) {
            buffer.writeInt(-1);
            return;
        }
        buffer.writeInt(rgbStateVariant.color());
    }

    @Override
    public IStateVariant deserializeFrom(FriendlyByteBuf buffer) {
        return new RGBStateVariant(buffer.readInt());
    }

    @Override
    public Optional<ItemStack> getItemStack(IStateVariant stateVariant) {
        if (!(stateVariant instanceof RGBStateVariant rgbStateVariant)) {
            return Optional.empty();
        }
        ItemStack stack = new ItemStack(block.get());
        stack.getOrCreateTag().putInt("color", rgbStateVariant.color());
        return Optional.of(stack);
    }

    @Override
    public Optional<FluidInformation> getFluidInformation(IStateVariant stateVariant, long l) {
        return Optional.empty();
    }

    @Override
    public Optional<Component> getName(IStateVariant stateVariant) {
        return Optional.of(Component.literal("Test"));
    }
}
