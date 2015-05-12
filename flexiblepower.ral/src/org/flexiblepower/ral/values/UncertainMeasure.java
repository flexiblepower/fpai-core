package org.flexiblepower.ral.values;

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

    public UncertainMeasure(double value, Unit<Q> unit) {
        this(value, value, value, value, value, unit);
    }

    public UncertainMeasure(double mean, double standardDeviation, Unit<Q> unit) {
        this(mean - 2 * standardDeviation,
             mean - standardDeviation,
             mean,
             mean + standardDeviation,
             mean + 2 * standardDeviation,
             unit);
    }

    public UncertainMeasure(double the95pprLowerBound,
                            double the68pprLowerBound,
                            double mean,
                            double the68pprUpperBound,
                            double the95pprUpperBound,
                            Unit<Q> unit) {
        if (the95pprLowerBound > the68pprLowerBound) {
            throw new IllegalArgumentException("68PPRLowerBound must be smaller than 95PPRLowerBound");
        }
        if (the68pprLowerBound > mean) {
            throw new IllegalArgumentException("mean must be smaller than the68PPRLowerBound");
        }
        if (mean > the68pprUpperBound) {
            throw new IllegalArgumentException("the68PPRUpperBound must be smaller than mean");
        }
        if (the68pprUpperBound > the95pprUpperBound) {
            throw new IllegalArgumentException("the95PPRUpperBound must be smaller than the68PPRUpperBound");
        }

        the95PPRLowerBound = the95pprLowerBound;
        the68PPRLowerBound = the68pprLowerBound;
        this.mean = mean;
        the68PPRUpperBound = the68pprUpperBound;
        the95PPRUpperBound = the95pprUpperBound;
        this.unit = unit;
    }

    public Measure<Double, Q> getMean() {
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
    public Measure<Double, Q> getStandardDeviation() {
        return Measure.valueOf(this.mean - this.the68PPRLowerBound, this.unit);
    }

    public Measure<Double, Q> get68PPRLowerBound() {
        return Measure.valueOf(this.the68PPRLowerBound, this.unit);
    }

    public Measure<Double, Q> get68PPRUpperBound() {
        return Measure.valueOf(this.the68PPRUpperBound, this.unit);
    }

    public boolean isIn68PPR(Measurable<Q> measure) {
        double v = measure.doubleValue(unit);
        return (v >= the68PPRLowerBound && v <= the68PPRUpperBound);
    }

    public Measure<Double, Q> get95PPRLowerBound() {
        return Measure.valueOf(this.the95PPRLowerBound, this.unit);
    }

    public Measure<Double, Q> get95PPRUpperBound() {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(mean);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(the68PPRLowerBound);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(the68PPRUpperBound);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(the95PPRLowerBound);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(the95PPRUpperBound);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        UncertainMeasure other = (UncertainMeasure) obj;
        if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean)) {
            return false;
        }
        if (Double.doubleToLongBits(the68PPRLowerBound) != Double.doubleToLongBits(other.the68PPRLowerBound)) {
            return false;
        }
        if (Double.doubleToLongBits(the68PPRUpperBound) != Double.doubleToLongBits(other.the68PPRUpperBound)) {
            return false;
        }
        if (Double.doubleToLongBits(the95PPRLowerBound) != Double.doubleToLongBits(other.the95PPRLowerBound)) {
            return false;
        }
        if (Double.doubleToLongBits(the95PPRUpperBound) != Double.doubleToLongBits(other.the95PPRUpperBound)) {
            return false;
        }
        if (unit == null) {
            if (other.unit != null) {
                return false;
            }
        } else if (!unit.equals(other.unit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mean).append(' ').append(unit);
        if (the68PPRLowerBound < mean) {
            sb.append(" (95% bounds = ")
              .append(the95PPRLowerBound)
              .append(" - ")
              .append(the95PPRUpperBound)
              .append(' ')
              .append(unit)
              .append(')');
        }
        return sb.toString();
    }
}
