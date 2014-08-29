package org.flexiblepower.rai.values;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Volume;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

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

    public static class Builder<BQ extends Quantity, FQ extends Quantity> {
        private final Commodity<BQ, FQ> commodity;

        private final List<CommodityForecastElement<BQ, FQ>> elements;
        private UncertainMeasure<Duration> duration;
        private Unit<FQ> unit;

        public Builder(Commodity<BQ, FQ> commodity) {
            this.commodity = commodity;
            this.elements = new ArrayList<CommodityForecastElement<BQ, FQ>>();
        }

        public Builder<BQ, FQ> set(UncertainMeasure<Duration> duration) {
            this.duration = duration;
            return this;
        }

        public Builder<BQ, FQ> setUnit(Unit<FQ> unit) {
            this.unit = unit;
            return this;
        }

        public Builder<BQ, FQ> add(UncertainMeasure<Duration> duration, UncertainMeasure<FQ> value) {
            elements.add(new CommodityForecastElement<BQ, FQ>(commodity, duration, value));
            return this;
        }

        public Builder<BQ, FQ> add(UncertainMeasure<FQ> value) {
            if (duration == null) {
                throw new IllegalStateException("duration not set");
            }
            elements.add(new CommodityForecastElement<BQ, FQ>(commodity, duration, value));
            return this;
        }

        public Builder<BQ, FQ> add(double mean, double standardDeviation) {
            if (duration == null) {
                throw new IllegalStateException("duration not set");
            } else if (unit == null) {
                throw new IllegalStateException("unit not set");
            }
            elements.add(new CommodityForecastElement<BQ, FQ>(commodity,
                                                              duration,
                                                              new UncertainMeasure<FQ>(mean, standardDeviation, unit)));
            return this;
        }

        @SuppressWarnings("unchecked")
        public CommodityForecast<BQ, FQ> build() {
            return new CommodityForecast<BQ, FQ>(elements.toArray(new CommodityForecastElement[0]));
        }
    }

    public static <BQ extends Quantity, FQ extends Quantity> Builder<BQ, FQ> create(Commodity<BQ, FQ> commodity) {
        return new Builder<BQ, FQ>(commodity);
    }

    public static class CommodityForecastElement<BQ extends Quantity, FQ extends Quantity> implements
                                                                                           ProfileElement<CommodityForecastElement<BQ, FQ>> {

        private final Commodity<BQ, FQ> commodity;
        private final UncertainMeasure<Duration> duration;
        private final UncertainMeasure<FQ> value;

        public CommodityForecastElement(Commodity<BQ, FQ> commodity,
                                        UncertainMeasure<Duration> duration,
                                        UncertainMeasure<FQ> value) {
            super();
            this.commodity = commodity;
            this.duration = duration;
            this.value = value;
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

        public UncertainMeasure<FQ> getValue() {
            return value;
        }

        public UncertainMeasure<Duration> getUncertainDuration() {
            return duration;
        }

    }

    public CommodityForecast(CommodityForecastElement<BQ, FQ>[] elements) {
        super(elements);
        validate();
    }

    private void validate() {
        // Check if profile is empty
        if (elements.length == 0) {
            throw new IllegalArgumentException("A CommodityForecast cannot be empty");
        }
        // Check if all the commodities are the same
        final Commodity<BQ, FQ> commodity = elements[0].getCommodity();
        for (int i = 1; i < elements.length; i++) {
            if (elements[i].getCommodity() != commodity) {
                throw new IllegalArgumentException("A CommodityForceast can only consist of commodites of the same type");
            }
        }
    }

    public Commodity<BQ, FQ> getCommodity() {
        // Validate makes sure there is at least one element
        return elements[0].getCommodity();
    }

    public CommodityForecast<BQ, FQ> concat(CommodityForecast<BQ, FQ> that) {
        if (this.getCommodity() != that.getCommodity()) {
            throw new IllegalArgumentException("Cannot concatenate CommodityForecasts of different types");
        }
        Builder<BQ, FQ> builder = CommodityForecast.create(this.getCommodity());
        for (CommodityForecastElement<BQ, FQ> e : elements) {
            builder.add(e.getUncertainDuration(), e.getValue());
        }
        for (CommodityForecastElement<BQ, FQ> e : that.elements) {
            builder.add(e.getUncertainDuration(), e.getValue());
        }
        return builder.build();
    }

    public UncertainMeasure<FQ> getValueAtOffset(Measurable<Duration> offset) {
        long targetOffset = offset.longValue(SI.MILLI(SI.SECOND));
        long currentOffset = 0;
        for (CommodityForecastElement<BQ, FQ> e : elements) {
            long elementLength = e.getDuration().longValue(SI.MILLI(SI.SECOND));
            if (targetOffset >= currentOffset && targetOffset < targetOffset + elementLength) {
                return e.getValue();
            }
            currentOffset += elementLength;
        }
        return null;
    }
}
