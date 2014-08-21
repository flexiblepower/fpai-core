package org.flexiblepower.rai.values;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Volume;
import javax.measure.quantity.VolumetricFlowRate;

import org.flexiblepower.rai.values.CommodityForecast.CommodityForecastElement;

/**
 * Class for representing an commodity consumption / production forecast over time. This class is similar to
 * {@link ProfileElement}, with the addition of uncertainty (see {@link UncertainMeasure}) in both amount and time.
 *
 * @param <BQ>
 *            Billable Quantity, see {@link Commodity}
 * @param <FQ>
 *            Flow Quantity, see {@link Commodity}
 */
public class CommodityForecast<BQ extends Quantity, FQ extends Quantity> extends
                                                                         Profile<CommodityForecastElement<BQ, FQ>> {
    public static class Map extends Commodity.Map<CommodityForecast<?, ?>> {

        public Map(CommodityForecast<Energy, Power> electricityValue,
                   CommodityForecast<Volume, VolumetricFlowRate> gasValue) {
            super(electricityValue, gasValue);
        }

        public <BQ extends Quantity, FQ extends Quantity> CommodityForecast<BQ, FQ> get(Commodity<BQ, FQ> key) {
            return get(key);
        }
    }

    public static class CommodityForecastElement<BQ extends Quantity, FQ extends Quantity> implements
                                                                                           ProfileElement<CommodityForecastElement<BQ, FQ>> {

        private final Commodity<BQ, FQ> commodity;
        private final UncertainMeasure<Duration> duration;
        private final UncertainMeasure<BQ> amount;

        public CommodityForecastElement(Commodity<BQ, FQ> commodity,
                                        UncertainMeasure<Duration> duration,
                                        UncertainMeasure<BQ> amount) {
            super();
            this.commodity = commodity;
            this.duration = duration;
            this.amount = amount;
        }

        @Override
        public Measurable<Duration> getDuration() {
            return duration.getMean();
        }

        @Override
        public CommodityForecastElement<BQ, FQ> subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
            // TODO implement
            throw new UnsupportedOperationException();
        }

        public Commodity<BQ, FQ> getCommodity() {
            return commodity;
        }

        public UncertainMeasure<BQ> getAmount() {
            return amount;
        }

        public Measure<Double, BQ> getExpectedAmount() {
            return amount.getMean();
        }

        public Measure<Double, FQ> getExpectedAverage() {
            double expected = commodity.average(getExpectedAmount(), getDuration())
                                       .doubleValue(commodity.getFlowUnit());
            return Measure.valueOf(expected, commodity.getFlowUnit());
        }

        public UncertainMeasure<Duration> getUncertainDuration() {
            return duration;
        }

    }

    public CommodityForecast(CommodityForecastElement<BQ, FQ>[] elements) {
        super(elements);
    }

}
