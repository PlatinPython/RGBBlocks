package platinpython.rgbblocks.util.top;

import java.util.function.Function;

import mcjty.theoneprobe.api.ITheOneProbe;

public class TOPMain implements Function<ITheOneProbe, Void>{
	static ITheOneProbe PROBE;

	@Override
	public Void apply(ITheOneProbe o) {
		PROBE = (ITheOneProbe) o;
		PROBE.registerProvider(new RGBBlockProvider());
		return null;
	}

}
