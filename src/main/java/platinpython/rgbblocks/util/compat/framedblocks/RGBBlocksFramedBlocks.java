package platinpython.rgbblocks.util.compat.framedblocks;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.data.BlockFamilies;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

import java.util.Set;
import java.util.function.Supplier;

public class RGBBlocksFramedBlocks {
    public static final Supplier<Set<Block>> VALID_BLOCKS = Suppliers.memoize(
        () -> BlockFamilies.getAllFamilies().map(BlockFamily::getBaseBlock).collect(ImmutableSet.toImmutableSet())
    );

    public static final DeferredRegister<CamoContainerFactory<?>> CAMO_CONTAINER_FACTORIES =
        DeferredRegister.create(FramedConstants.Registries.CAMO_CONTAINER_FACTORY_REGISTRY_KEY, RGBBlocks.MOD_ID);

    public static final DeferredHolder<CamoContainerFactory<?>, RGBBlocksCamoContainer.Factory> RGBBLOCKS_CONTAINER_FACTORY =
        CAMO_CONTAINER_FACTORIES.register("container_factory", RGBBlocksCamoContainer.Factory::new);

    public static void register(IEventBus bus) {
        RGBBlocksFramedBlocks.CAMO_CONTAINER_FACTORIES.register(bus);
    }

    public static InteractionResult handlePaintBucketInteraction(UseOnContext context) {
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof FramedBlockEntity framedBlockEntity)) {
            return InteractionResult.PASS;
        }
        BlockHitResult blockHitResult = new BlockHitResult(
            context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()
        );
        if (context.getPlayer() == null) {
            return InteractionResult.PASS;
        }
        if (!(framedBlockEntity
            .getCamo(blockHitResult, context.getPlayer()) instanceof RGBBlocksCamoContainer camoContainer)) {
            return InteractionResult.PASS;
        }
        if (!context.getPlayer().isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        context.getItemInHand().set(DataComponentRegistry.COLOR, camoContainer.color);
        return InteractionResult.SUCCESS;
    }
}
