package org.flexiblepower.rai.values;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

public class ConstraintList<Q extends Quantity> extends AbstractList<Constraint<Q>> {
    private final Constraint<Q>[] powerConstraints;

    @SuppressWarnings("unchecked")
    public ConstraintList(List<Constraint<Q>> list) {
        powerConstraints = list.toArray(new Constraint[list.size()]);
    }

    public ConstraintList(Constraint<Q>... list) {
        powerConstraints = Arrays.copyOf(list, list.length);
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
