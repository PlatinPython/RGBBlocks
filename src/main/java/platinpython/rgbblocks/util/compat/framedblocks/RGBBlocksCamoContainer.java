package platinpython.rgbblocks.util.compat.framedblocks;

import com.google.common.base.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.RegistryHandler;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.camo.CamoContainerType;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RGBBlocksCamoContainer extends CamoContainer {
    int color;

    protected RGBBlocksCamoContainer(BlockState state, int color) {
        super(state);
        this.color = color;
    }

    @Override
    public float[] getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return new Color(this.color).getRGBColorComponents();
    }

    @Override
    public int getColor(BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        return color;
    }

    @Override
    public ItemStack toItemStack(ItemStack _ignored) {
        ItemStack stack = new ItemStack(state.getBlock());
        stack.getOrCreateTag().putInt("color", color);
        return stack;
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
        return color == that.color && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.state, this.color);
    }

    @Override
    public CamoContainerType getType() {
        return CamoContainerType.BLOCK;
    }

    @Override
    public Factory getFactory() {
        return RGBBlocksFramedBlocks.RGBBLOCKS_CONTAINER_FACTORY.get();
    }

    @Override
    public void save(CompoundTag tag) {
        tag.put("state", NbtUtils.writeBlockState(state));
        tag.putInt("color", color);
    }

    @Override
    public void toNetwork(CompoundTag tag) {
        tag.putInt("state", Block.getId(state));
        tag.putInt("color", color);
    }

    public static class Factory extends CamoContainerFactory {
        @Override
        public CamoContainer fromNbt(CompoundTag tag) {
            return new RGBBlocksCamoContainer(
                NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("state")),
                tag.getInt("color")
            );
        }

        @Override
        public CamoContainer fromNetwork(CompoundTag tag) {
            return new RGBBlocksCamoContainer(Block.stateById(tag.getInt("state")), tag.getInt("color"));
        }

        @Override
        public CamoContainer fromItem(ItemStack stack) {
            if (stack.getItem() instanceof RGBBlockItem item) {
                return new RGBBlocksCamoContainer(
                    item.getBlock().defaultBlockState(), stack.getOrCreateTag().getInt("color")
                );
            }
            return EmptyCamoContainer.EMPTY;
        }

        @Override
        public void registerTriggerItems(Consumer<Item> registrar) {
            RegistryHandler.ITEMS.getEntries()
                .stream()
                .map(Supplier::get)
                .filter(i -> i instanceof RGBBlockItem)
                .forEach(registrar);
        }
    }
}
