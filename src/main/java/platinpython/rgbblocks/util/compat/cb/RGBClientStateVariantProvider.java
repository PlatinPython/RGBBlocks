package platinpython.rgbblocks.util.compat.cb;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.models.data.IModelDataBuilder;
import com.communi.suggestu.scena.core.client.models.data.IModelDataKey;
import mod.chiselsandbits.api.client.variant.state.IClientStateVariantProvider;
import mod.chiselsandbits.api.variant.state.IStateVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class RGBClientStateVariantProvider implements IClientStateVariantProvider {
    private static final IModelDataKey<Integer> COLOR = IModelDataKey.create();

    @Override
    public IBlockModelData getBlockModelData(IStateVariant stateVariant) {
        if (!(stateVariant instanceof RGBStateVariant rgbStateVariant)) {
            return IBlockModelData.empty();
        }

        return IModelDataBuilder.create().withInitial(COLOR, rgbStateVariant.color()).build();
    }

    @Override
    public void appendHoverText(IStateVariant stateVariant, Level level, List<Component> list, TooltipFlag flag) {}
}
