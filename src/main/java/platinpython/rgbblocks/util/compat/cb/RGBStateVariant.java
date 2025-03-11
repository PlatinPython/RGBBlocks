package platinpython.rgbblocks.util.compat.cb;

import mod.chiselsandbits.api.variant.state.IStateVariant;

public record RGBStateVariant(int color) implements IStateVariant {
    public static final RGBStateVariant WHITE = new RGBStateVariant(-1);

    @Override
    public int compareTo(IStateVariant o) {
        if (!(o instanceof RGBStateVariant rgbStateVariant)) {
            return -1;
        }
        return this.color - rgbStateVariant.color;
    }

    @Override
    public IStateVariant createSnapshot() {
        return this;
    }
}
