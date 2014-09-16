package org.flexiblepower.rai.values;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * The {@link ConstraintList} is used as a method to describe several modes. It is most commonly used for representing
 * different possible charge speeds in the {@link BufferControlSpace}. This is a list of several values or ranges of
 * values.
 *
 * @param <Q>
 *            The quantity of the values that are stored here.
 */
public class ConstraintList<Q extends Quantity> extends AbstractList<Constraint<Q>> {
    /**
     * Starts a new builder for constructing a {@link ConstraintList} using a default unit value.
     *
     * @param unit
     *            The default unit value that will be used in the {@link Builder#addSingle(double)} and
     *            {@link Builder#addRange(double, double)} methods.
     * @return The new builder.
     */
    public static <Q extends Quantity> Builder<Q> create(Unit<Q> unit) {
        return new Builder<Q>(unit);
    }

    /**
     * The {@link Builder} is a convenience class to easily construct new immutable {@link ConstraintList}s.
     *
     * @author TNO
     *
     * @param <Q>
     *            The quantity of the values that are stored here
     */
    public static class Builder<Q extends Quantity> {
        private final Unit<Q> defaultUnit;
        private final List<Constraint<Q>> commodityConstraints;

        Builder(Unit<Q> defaultUnit) {
            this.defaultUnit = defaultUnit;
            commodityConstraints = new ArrayList<Constraint<Q>>();
        }

        /**
         * Adds a single value to the list.
         *
         * @param value
         *            The value to be added
         * @return this
         */
        public Builder<Q> addSingle(Measurable<Q> value) {
            commodityConstraints.add(new Constraint<Q>(value));
            return this;
        }

        /**
         * Adds a single value to the list using the default unit.
         *
         * @param value
         *            The value to be added
         * @return this
         */
        public Builder<Q> addSingle(double value) {
            commodityConstraints.add(new Constraint<Q>(Measure.valueOf(value, defaultUnit)));
            return this;
        }

        /**
         * Adds a ranged value to the list.
         *
         * @param lowerBound
         *            The lower bound of the range
         * @param upperBound
         *            The upper bound of the range
         * @return this
         */
        public Builder<Q> addRange(Measurable<Q> lowerBound, Measurable<Q> upperBound) {
            commodityConstraints.add(new Constraint<Q>(lowerBound, upperBound));
            return this;
        }

        /**
         * Adds a ranged value to the list using the default unit.
         *
         * @param lowerBound
         *            The lower bound of the range
         * @param upperBound
         *            The upper bound of the range
         * @return this
         */
        public Builder<Q> addRange(double lowerBound, double upperBound) {
            commodityConstraints.add(new Constraint<Q>(Measure.valueOf(lowerBound, defaultUnit),
                                                       Measure.valueOf(upperBound, defaultUnit)));
            return this;
        }

        /**
         * @return The immutable {@link ConstraintList} that contains the values from this Builder.
         */
        public ConstraintList<Q> build() {
            return new ConstraintList<Q>(commodityConstraints);
        }
    }

    private final Constraint<Q>[] commodityConstraints;

    /**
     * @param list
     *            Creates a new {@link ConstraintList} from a given list of constraints.
     */
    @SuppressWarnings("unchecked")
    public ConstraintList(List<Constraint<Q>> list) {
        commodityConstraints = list.toArray(new Constraint[list.size()]);
    }

    @Override
    public Constraint<Q> get(int index) {
        return commodityConstraints[index];
    }

    @Override
    public int size() {
        return commodityConstraints.length;
    }

    /**
     * @return The lowest lower bound of any {@link Constraint} in this list.
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
     * @return The highest upper bound of any {@link Constraint} in this list.
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
