package org.flexiblepower.rai.values;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class ConstraintList<Q extends Quantity> extends AbstractList<Constraint<Q>> {
    public static <Q extends Quantity> Builder<Q> create(Unit<Q> unit) {
        return new Builder<Q>(unit);
    }

    public static class Builder<Q extends Quantity> {
        private final Unit<Q> defaultUnit;
        private final List<Constraint<Q>> powerConstraints;

        public Builder(Unit<Q> defaultUnit) {
            this.defaultUnit = defaultUnit;
            powerConstraints = new ArrayList<Constraint<Q>>();
        }

        public Builder<Q> addSingle(Measurable<Q> value) {
            powerConstraints.add(new Constraint<Q>(value));
            return this;
        }

        public Builder<Q> addSingle(double value) {
            powerConstraints.add(new Constraint<Q>(Measure.valueOf(value, defaultUnit)));
            return this;
        }

        public Builder<Q> addRange(Measurable<Q> lowerBound, Measurable<Q> upperBound) {
            powerConstraints.add(new Constraint<Q>(lowerBound, upperBound));
            return this;
        }

        public Builder<Q> addRange(double lowerBound, double upperBound) {
            powerConstraints.add(new Constraint<Q>(Measure.valueOf(lowerBound, defaultUnit),
                                                   Measure.valueOf(upperBound, defaultUnit)));
            return this;
        }

        public ConstraintList<Q> build() {
            return new ConstraintList<Q>(powerConstraints);
        }
    }

    private final Constraint<Q>[] powerConstraints;

    @SuppressWarnings("unchecked")
    public ConstraintList(List<Constraint<Q>> list) {
        powerConstraints = list.toArray(new Constraint[list.size()]);
    }

    @Override
    public Constraint<Q> get(int index) {
        return powerConstraints[index];
    }

    @Override
    public int size() {
        return powerConstraints.length;
    }

    /**
     * @return The minimum allowed power level according to the list of power constraints.
     */
    public Measurable<Q> getMinimum() {
        Measurable<Q> min = null;

        for (Constraint<Q> constraint : this) {
            if (min == null || constraint.getLowerBound().compareTo(min) < 0) {
                min = constraint.getLowerBound();
            }
        }

        return min;
    }

    /**
     * @return The maximum allowed power level according to the list of power constraints.
     */
    public Measurable<Q> getMaximum() {
        Measurable<Q> max = null;

        for (Constraint<Q> constraint : this) {
            if (max == null || constraint.getUpperBound().compareTo(max) > 0) {
                max = constraint.getUpperBound();
            }
        }

        return max;
    }
}
