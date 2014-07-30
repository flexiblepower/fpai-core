package org.flexiblepower.rai.values;

import java.io.Serializable;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * This class is being used to express uncertainty about a value. It does not prescribe a particular distribution, such
 * as a normal distribution. Instead UncertainMeasure provides the mean value as well as the ranges in which 68 and 95
 * percent of the values fall. For both ranges a lower and upper bound is given. This flexible solution also allows the
 * expression of asymmetric distributions.
 * 
 * @author TNO
 * 
 * @param <Q>
 *            Quantity of measurement, see {@link Commodity}
 */

public class UncertainMeasure<Q extends Quantity> implements Serializable, Measurable<Q> {

    private static final long serialVersionUID = -6483611366956749285L;

    private final double the95PPRLowerBound;
    private final double the68PPRLowerBound;
    private final double mean;
    private final double the68PPRUpperBound;
    private final double the95PPRUpperBound;
    private final Unit<Q> unit;

    public UncertainMeasure(double the95pprLowerBound,
                            double the68pprLowerBound,
                            double mean,
                            double the68pprUpperBound,
                            double the95pprUpperBound,
                            Unit<Q> unit) {
        super();
        the95PPRLowerBound = the95pprLowerBound;
        the68PPRLowerBound = the68pprLowerBound;
        this.mean = mean;
        the68PPRUpperBound = the68pprUpperBound;
        the95PPRUpperBound = the95pprUpperBound;
        this.unit = unit;
        validate();
    }

    public UncertainMeasure(double mean, double standardDeviation, Unit<Q> unit) {
        this.the95PPRLowerBound = mean - 2 * standardDeviation;
        this.the68PPRLowerBound = mean - standardDeviation;
        this.mean = mean;
        this.the68PPRUpperBound = mean + standardDeviation;
        this.the95PPRUpperBound = mean + 2 * standardDeviation;
        this.unit = unit;
        validate();
    }

    public UncertainMeasure(double value, Unit<Q> unit) {
        this.the95PPRLowerBound = value;
        this.the68PPRLowerBound = value;
        this.mean = value;
        this.the68PPRUpperBound = value;
        this.the95PPRUpperBound = value;
        this.unit = unit;
        validate();
    }

    private void validate() {
        if (the95PPRLowerBound > the68PPRLowerBound) {
            throw new IllegalArgumentException("68PPRLowerBound must be smaller than 95PPRLowerBound");
        }
        if (the68PPRLowerBound > mean) {
            throw new IllegalArgumentException("mean must be smaller than the68PPRLowerBound");
        }
        if (mean > the68PPRUpperBound) {
            throw new IllegalArgumentException("the68PPRUpperBound must be smaller than mean");
        }
        if (the68PPRUpperBound > the95PPRUpperBound) {
            throw new IllegalArgumentException("the95PPRUpperBound must be smaller than the68PPRUpperBound");
        }
    }

    public Measurable<Q> getMean() {
        return Measure.valueOf(this.mean, this.unit);
    }

    public Unit<Q> getUnit() {
        return unit;
    }

    /**
     * Gets the standard deviaton. WARNING: ASSUMES NORMAL DISTRIBUTION
     * 
     * @return Standard deviation of this probability distribution
     */
    public Measurable<Q> getStandardDeviation() {
        return Measure.valueOf(this.mean - this.the68PPRLowerBound, this.unit);
    }

    public Measurable<Q> get68PPRLowerBound() {
        return Measure.valueOf(this.the68PPRLowerBound, this.unit);
    }

    public Measurable<Q> get68PPRUpperBound() {
        return Measure.valueOf(this.the68PPRUpperBound, this.unit);
    }

    public boolean isIn68PPR(Measurable<Q> measure) {
        double v = measure.doubleValue(unit);
        return (v >= the68PPRLowerBound && v <= the68PPRUpperBound);
    }

    public Measurable<Q> get95PPRLowerBound() {
        return Measure.valueOf(this.the95PPRLowerBound, this.unit);
    }

    public Measurable<Q> get95PPRUpperBound() {
        return Measure.valueOf(this.the95PPRUpperBound, this.unit);
    }

    public boolean isIn95PPR(Measurable<Q> measure) {
        double v = measure.doubleValue(unit);
        return (v >= the95PPRLowerBound && v <= the95PPRUpperBound);
    }

    @Override
    public int compareTo(Measurable<Q> other) {
        return Double.compare(doubleValue(unit), other.doubleValue(unit));
    }

    @Override
    public double doubleValue(Unit<Q> unit) {
        return getMean().doubleValue(unit);
    }

    @Override
    public long longValue(Unit<Q> unit) throws ArithmeticException {
        return (long) doubleValue(unit);
    }

    @Override
    public Measurable<Q> add(Measurable<Q> other) {
        if (other instanceof UncertainMeasure) {
            UncertainMeasure<Q> that = (UncertainMeasure<Q>) other;
            // Assume normal distribution
            // See http://en.wikipedia.org/wiki/Sum_of_normally_distributed_random_variables
            double thisSd = this.getStandardDeviation().doubleValue(unit);
            double thatSd = that.getStandardDeviation().doubleValue(unit);
            double newSd = Math.sqrt(Math.pow(thisSd, 2) + Math.pow(thatSd, 2));
            double newMean = this.getMean().doubleValue(unit) + that.getMean().doubleValue(unit);
            return new UncertainMeasure<Q>(newMean, newSd, unit);
        } else {
            // User add method from other
            return other.add(this);
        }
    }
}
