package platinpython.rgbblocks.util.top;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

public class TOPMain implements Function<ITheOneProbe, Void> {
    private static ITheOneProbe probe;

    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        probe = theOneProbe;
        probe.registerProvider(new RGBBlockProvider());
        return null;
    }
}
