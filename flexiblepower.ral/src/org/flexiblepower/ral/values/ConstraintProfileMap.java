package org.flexiblepower.ral.values;

import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.VolumetricFlowRate;

/**
 * The {@link ConstraintProfileMap} is used to store a {@link ConstraintProfile} for each {@link Commodity}.
 */
public final class ConstraintProfileMap extends CommodityMap<ConstraintProfile<?>> {
    /**
     * <p>
     * This helper class makes it easy to create an instance of the {@link ConstraintProfileMap}. To create an instance
     * of this class, use {@link ConstraintProfileMap#create()}.
     * </p>
     *
     * <p>
     * Typical usage looks like this:
     * </p>
     *
     * <p>
     * <code>ConstraintProfileMap map = ConstraintProfileMap.create().electricity(electricityProfile).gas(gasProfile).build();</code>
     * </p>
     */
    public static class Builder {
        private ConstraintProfile<Power> electricityValue, heatValue;
        private ConstraintProfile<VolumetricFlowRate> gasValue;

        protected Builder() {
        }

        public Builder electricity(ConstraintProfile<Power> value) {
            electricityValue = value;
            return this;
        }

        public Builder gas(ConstraintProfile<VolumetricFlowRate> value) {
            gasValue = value;
            return this;
        }

        public Builder heat(ConstraintProfile<Power> value) {
            heatValue = value;
            return this;
        }

        public ConstraintProfileMap build() {
            return new ConstraintProfileMap(electricityValue, gasValue, heatValue);
        }
    }

    /**
     * @return A new {@link Builder} object that can be used to create the {@link ConstraintProfileMap} more easily.
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * @param electricityValue
     *            The {@link ConstraintList} of the electricity
     * @return A {@link ConstraintProfileMap} which only contains a value for {@link Commodity#ELECTRICITY}
     */
    public static ConstraintProfileMap electricity(ConstraintProfile<Power> electricityValue) {
        return new ConstraintProfileMap(electricityValue, null, null);
    }

    /**
     * @param gasValue
     *            The {@link ConstraintList} of the gas
     * @return A {@link ConstraintProfileMap} which only contains a value for {@link Commodity#GAS}
     */
    public static ConstraintProfileMap gas(ConstraintProfile<VolumetricFlowRate> gasValue) {
        return new ConstraintProfileMap(null, gasValue, null);
    }

    /**
     * @param heatValue
     *            The {@link ConstraintList} of the heat
     * @return A {@link ConstraintProfileMap} which only contains a value for {@link Commodity#HEAT}
     */
    public static ConstraintProfileMap heat(ConstraintProfile<Power> heatValue) {
        return new ConstraintProfileMap(null, null, heatValue);
    }

    ConstraintProfileMap(ConstraintProfile<Power> electricityValue,
                         ConstraintProfile<VolumetricFlowRate> gasValue,
                         ConstraintProfile<Power> heatValue) {
        super(electricityValue, gasValue, heatValue);
    }

    @SuppressWarnings("unchecked")
    public <BQ extends Quantity, FQ extends Quantity> ConstraintProfile<FQ> get(Commodity<BQ, FQ> commodity) {
        return (ConstraintProfile<FQ>) super.get(commodity);
    }
}
