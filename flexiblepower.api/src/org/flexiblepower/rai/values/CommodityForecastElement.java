package org.flexiblepower.rai.values;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

public class CommodityForecastElement<BQ extends Quantity, FQ extends Quantity> implements
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
    public CommodityForecastElement subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public UncertainMeasure<Duration> getUncertainDuration() {
        return duration;
    }

}
