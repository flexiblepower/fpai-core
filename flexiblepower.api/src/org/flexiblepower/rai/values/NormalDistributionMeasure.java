package org.flexiblepower.rai.values;

import java.io.Serializable;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class NormalDistributionMeasure<Q extends Quantity> implements Serializable {

    private static final long serialVersionUID = -6483611366956749285L;

    private final double mean;
    private final double standardDeviation;
    private final Unit<Q> unit;

    public NormalDistributionMeasure(double mean, double standardDeviation, Unit<Q> unit) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.unit = unit;
    }

    public Measurable<Q> getMean() {
        return Measure.valueOf(this.mean, this.unit);
    }

    public Measurable<Q> getStandardDeviation() {
        return Measure.valueOf(this.mean, this.unit);
    }

    public Unit<Q> getUnit() {
        return unit;
    }

    public Measurable<Q> getLowerBoundFor68() {
        return Measure.valueOf(this.mean - this.standardDeviation, this.unit);
    }

    public Measurable<Q> getUpperBoundFor68() {
        return Measure.valueOf(this.mean + this.standardDeviation, this.unit);
    }

    public Measurable<Q> getLowerBoundFor95() {
        return Measure.valueOf(this.mean - 2 * this.standardDeviation, this.unit);
    }

    public Measurable<Q> getUpperBoundFor95() {
        return Measure.valueOf(this.mean + 2 * this.standardDeviation, this.unit);
    }

}
