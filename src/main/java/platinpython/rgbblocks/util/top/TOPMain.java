package platinpython.rgbblocks.util.top;

import java.util.function.Function;

import mcjty.theoneprobe.api.ITheOneProbe;

public class TOPMain implements Function<ITheOneProbe, Void> {
    private static ITheOneProbe probe;

    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        probe = theOneProbe;
        probe.registerProvider(new RGBBlockProvider());
        return null;
    }

}
