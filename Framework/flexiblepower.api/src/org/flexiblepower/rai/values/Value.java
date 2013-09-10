package org.flexiblepower.rai.values;

import org.flexiblepower.rai.unit.Unit;

/**
 * AbstractMeasurement is abstract class implementing common functionality of Measurement.
 * 
 * PMSuite - PM Data Specification - v0.6
 */
public abstract class Value<U extends Unit<U>> implements Comparable<Value<U>> {

    private final double value;

    private final U unit;

    public Value(double value, U unit) {
        if (unit == null) {
            throw new NullPointerException();
        }

        this.value = value;
        this.unit = unit;
    }

    public U getUnit() {
        return unit;
    }

    public double getValue() {
        return value;
    }

    public double getValueAs(U targetUnit) {
        return getUnit().convertTo(getValue(), targetUnit);
    }

    public abstract double getValueAsDefaultUnit();

    @Override
    public int compareTo(Value<U> other) {
        double a = getValue();
        double b = other.getValueAs(getUnit());
        return (a == b) ? 0 : (a < b ? -1 : 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            Value<?> other = (Value<?>) obj;
            if (getUnit().getClass() != other.getUnit().getClass()) {
                return false;
            } else {
                @SuppressWarnings("unchecked")
                Value<U> m = (Value<U>) obj;
                double thisValue = this.getValueAs(m.getUnit());
                double otherValue = m.getValueAs(getUnit());
                return this.value == otherValue || other.value == thisValue;
            }
        }
    }

    public boolean equals(Value<U> target, Value<U> tolerance) {
        return equals(target.getValue(), target.getUnit(), tolerance.getValue(), tolerance.getUnit());
    }

    private boolean equals(double target, U unitTarget, double valueTolerance, U unitTolerance) {
        double v1 = getValueAs(unitTolerance);
        double v2 = unitTarget.convertTo(target, unitTolerance);
        double diff = Math.abs(v1 - v2);
        return diff <= valueTolerance;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return value + unit.getSymbol();
    }
}
