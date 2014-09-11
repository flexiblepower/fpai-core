package org.flexiblepower.rai.values;

import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.VolumetricFlowRate;

/**
 * The ConstraintListMap is used to store a {@link ConstraintList} for each {@link Commodity}.
 */
public final class ConstraintListMap extends CommodityMap<ConstraintList<?>> {
    /**
     * <p>
     * This helper class makes it easy to create an instance of the {@link ConstraintListMap}. To create an instance of
     * this class, use {@link ConstraintListMap#create()}.
     * </p>
     *
     * <p>
     * Typical usage looks like this:
     * </p>
     *
     * <p>
     * <code>ConstraintListMap map = ConstraintListMap.create().electricity(electricityList).gas(gasList).build();</code>
     * </p>
     */
    public static class Builder {
        private ConstraintList<Power> electricityValue, heatValue;
        private ConstraintList<VolumetricFlowRate> gasValue;

        protected Builder() {
        }

        public Builder electricity(ConstraintList<Power> value) {
            electricityValue = value;
            return this;
        }

        public Builder gas(ConstraintList<VolumetricFlowRate> value) {
            gasValue = value;
            return this;
        }

        public Builder heat(ConstraintList<Power> value) {
            heatValue = value;
            return this;
        }

        public ConstraintListMap build() {
            return new ConstraintListMap(electricityValue, gasValue, heatValue);
        }
    }

    public static final ConstraintListMap EMPTY = create().build();

    /**
     * @return A new {@link Builder} object that can be used to create the {@link ConstraintListMap} more easily.
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * @param electricityValue
     *            The {@link ConstraintList} of the electricity
     * @return A {@link ConstraintListMap} which only contains a value for {@link Commodity#ELECTRICITY}
     */
    public static ConstraintListMap electricity(ConstraintList<Power> electricityValue) {
        return new ConstraintListMap(electricityValue, null, null);
    }

    /**
     * @param gasValue
     *            The {@link ConstraintList} of the gas
     * @return A {@link ConstraintListMap} which only contains a value for {@link Commodity#GAS}
     */
    public static ConstraintListMap gas(ConstraintList<VolumetricFlowRate> gasValue) {
        return new ConstraintListMap(null, gasValue, null);
    }

    /**
     * @param heatValue
     *            The {@link ConstraintList} of the heat
     * @return A {@link ConstraintListMap} which only contains a value for {@link Commodity#HEAT}
     */
    public static ConstraintListMap heat(ConstraintList<Power> heatValue) {
        return new ConstraintListMap(null, null, heatValue);
    }

    protected ConstraintListMap(ConstraintList<Power> electricityValue,
                                ConstraintList<VolumetricFlowRate> gasValue,
                                ConstraintList<Power> heatValue) {
        super(electricityValue, gasValue, heatValue);
    }

    public <BQ extends Quantity, FQ extends Quantity> ConstraintList<FQ> get(Commodity<BQ, FQ> commodity) {
        return get(commodity);
    }
}
