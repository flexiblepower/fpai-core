package org.flexiblepower.efi.util;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

import org.flexiblepower.rai.values.Commodity;

public class CommodityProfileElement<BQ extends Quantity, FQ extends Quantity> implements
                                                                               ProfileElement<CommodityProfileElement<BQ, FQ>> {

    private final Commodity<BQ, FQ> commodity;
    private final Measurable<Duration> duration;
    private final Measurable<BQ> amount;

    public CommodityProfileElement(Commodity<BQ, FQ> commodity, Measurable<Duration> duration, Measurable<BQ> amount) {
        super();
        this.commodity = commodity;
        this.duration = duration;
        this.amount = amount;
    }

    @Override
    public Measurable<Duration> getDuration() {
        return duration;
    }

    @Override
    public CommodityProfileElement<BQ, FQ> subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
        final double oldDurationDouble = this.duration.doubleValue(Duration.UNIT);
        final double newDurationDouble = duration.doubleValue(Duration.UNIT);
        final double amountDouble = amount.doubleValue(commodity.getBillableUnit());
        final double newAmountDouble = amountDouble / oldDurationDouble * newDurationDouble;
        final Measure<Double, BQ> newAmount = Measure.valueOf(newAmountDouble, commodity.getBillableUnit());
        return new CommodityProfileElement<BQ, FQ>(commodity, duration, newAmount);
    }

    public Measurable<BQ> getAmount() {
        return amount;
    }

    public Measurable<FQ> getAverage() {
        return commodity.average(amount, duration);
    }

    public Commodity<BQ, FQ> getCommodity() {
        return commodity;
    }

}
