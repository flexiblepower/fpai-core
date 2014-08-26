package org.flexiblepower.efi.buffer;

import java.util.ArrayList;
import java.util.List;

public class LeakageFunction extends FillLevelFunction<RangeElement> {
    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private final List<RangeElement> elements = new ArrayList<RangeElement>();

        Builder() {
        }

        public Builder add(RangeElement element) {
            elements.add(element);
            return this;
        }

        public Builder add(double lowerBound, double upperBound, double fillingSpeed) {
            elements.add(new RangeElement(lowerBound, upperBound, fillingSpeed));
            return this;
        }

        public LeakageFunction build() {
            return new LeakageFunction(elements.toArray(new RangeElement[elements.size()]));
        }
    }

    public LeakageFunction(RangeElement... rangeElements) {
        super(rangeElements);
    }

}
