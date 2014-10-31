package org.flexiblepower.rai.values;

import javax.measure.Measurable;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.VolumetricFlowRate;

/**
 * The CommodityMeasurable is used to store a {@link Measurable} for each {@link Commodity}.
 */
public final class CommodityMeasurables extends CommodityMap<Measurable<?>> {
    /**
     * <p>
     * This helper class makes it easy to create an instance of the {@link CommodityMeasurables}. To create an instance
     * of this class, use {@link CommodityMeasurables#create()}.
     * </p>
     *
     * <p>
     * Typical usage looks like this:
     * </p>
     *
     * <p>
     * <code>CommodityMeasurables measure = CommodityMeasurables.create().electricity(electricityMeasure).gas(gasMeasure).build();</code>
     * </p>
     */
    public static class Builder {
        private Measurable<Power> electricityValue, heatValue;
        private Measurable<VolumetricFlowRate> gasValue;

        protected Builder() {
        }

        public Builder electricity(Measurable<Power> value) {
            electricityValue = value;
            return this;
        }

        public Builder gas(Measurable<VolumetricFlowRate> value) {
            gasValue = value;
            return this;
        }

        public Builder heat(Measurable<Power> value) {
            heatValue = value;
            return this;
        }

        public CommodityMeasurables build() {
            return new CommodityMeasurables(electricityValue, gasValue, heatValue);
        }
    }

    /**
     * @return A new {@link Builder} object that can be used to create the {@link CommodityMeasurables} more easily.
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * @param electricityValue
     *            The measurable value of the electricity
     * @return A {@link CommodityMeasurables} which only contains a value for {@link Commodity#ELECTRICITY}
     */
    public static CommodityMeasurables electricity(Measurable<Power> electricityValue) {
        return new CommodityMeasurables(electricityValue, null, null);
    }

    /**
     * @param gasValue
     *            The measurable value of the gas
     * @return A {@link CommodityMeasurables} which only contains a value for {@link Commodity#GAS}
     */
    public static CommodityMeasurables gas(Measurable<VolumetricFlowRate> gasValue) {
        return new CommodityMeasurables(null, gasValue, null);
    }

    /**
     * @param heatValue
     *            The measurable value of the heat
     * @return A {@link CommodityMeasurables} which only contains a value for {@link Commodity#HEAT}
     */
    public static CommodityMeasurables heat(Measurable<Power> heatValue) {
        return new CommodityMeasurables(null, null, heatValue);
    }

    protected CommodityMeasurables(Measurable<Power> electricityValue,
                                   Measurable<VolumetricFlowRate> gasValue,
                                   Measurable<Power> heatValue) {
        super(electricityValue, gasValue, heatValue);
    }

    @SuppressWarnings("unchecked")
    public <BQ extends Quantity, FQ extends Quantity> Measurable<FQ> get(Commodity<BQ, FQ> commodity) {
        return (Measurable<FQ>) super.get(commodity);
    }
}
