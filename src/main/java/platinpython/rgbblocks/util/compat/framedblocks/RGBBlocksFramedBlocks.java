package platinpython.rgbblocks.util.compat.framedblocks;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.RegistryHandler;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.util.FramedConstants;

public class RGBBlocksFramedBlocks {
    public static final DeferredRegister<CamoContainer.Factory> CAMO_CONTAINER_FACTORIES =
        DeferredRegister.create(FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_NAME, RGBBlocks.MOD_ID);

    public static final RegistryObject<RGBBlocksCamoContainer.Factory> RGBBLOCKS_CONTAINER_FACTORY =
        CAMO_CONTAINER_FACTORIES.register("container_factory", RGBBlocksCamoContainer.Factory::new);

    @SubscribeEvent
    public static void registerFramedBlocksStuff(FMLCommonSetupEvent event) {
        RegistryHandler.ITEMS.getEntries()
            .stream()
            .map(RegistryObject::get)
            .filter(i -> i instanceof RGBBlockItem)
            .forEach(
                i -> FramedBlocksAPI.getInstance().registerCamoContainerFactory(i, RGBBLOCKS_CONTAINER_FACTORY.get())
            );
    }

    public static void register() {
        RGBBlocksFramedBlocks.CAMO_CONTAINER_FACTORIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().register(RGBBlocksFramedBlocks.class);
    }

    public static InteractionResult handlePaintBucketInteraction(UseOnContext context) {
        BlockEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(tileEntity instanceof FramedBlockEntity framedBlockEntity)) {
            return InteractionResult.PASS;
        }
        BlockHitResult blockHitResult = new BlockHitResult(
            context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()
        );
        if (!(framedBlockEntity.getCamo(blockHitResult) instanceof RGBBlocksCamoContainer camoContainer)) {
            return InteractionResult.PASS;
        }
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            context.getItemInHand().getOrCreateTag().putInt("color", camoContainer.color);
        } else {
            if (!context.getPlayer().getAbilities().instabuild
                && context.getItemInHand().getOrCreateTag().getInt("color") != camoContainer.color) {
                if (context.getItemInHand().getDamageValue() == context.getItemInHand().getMaxDamage() - 1) {
                    context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                } else {
                    context.getItemInHand()
                        .hurtAndBreak(1, context.getPlayer(), e -> e.broadcastBreakEvent(context.getHand()));
                }
            }
            camoContainer.color = context.getItemInHand().getOrCreateTag().getInt("color");
            context.getLevel()
                .sendBlockUpdated(
                    context.getClickedPos(), framedBlockEntity.getBlockState(), framedBlockEntity.getBlockState(),
                    Block.UPDATE_ALL_IMMEDIATE
                );
        }
        return InteractionResult.SUCCESS;
    }
}
