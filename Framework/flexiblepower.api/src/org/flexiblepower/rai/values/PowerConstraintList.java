package org.flexiblepower.rai.values;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import org.flexiblepower.rai.unit.PowerUnit;

public class PowerConstraintList extends AbstractList<PowerConstraint> {
    private final PowerConstraint[] powerConstraints;

    public PowerConstraintList(List<PowerConstraint> list) {
        powerConstraints = list.toArray(new PowerConstraint[list.size()]);
    }

    public PowerConstraintList(PowerConstraint... list) {
        powerConstraints = Arrays.copyOf(list, list.length);
    }

    @Override
    public PowerConstraint get(int index) {
        return powerConstraints[index];
    }

    @Override
    public int size() {
        return powerConstraints.length;
    }

    /**
     * @return The minimum allowed power level according to the list of power constraints.
     */
    public PowerValue getMinimum() {
        double min = Double.MAX_VALUE;

        for (PowerConstraint constraint : this) {
            double lowerBound = constraint.getLowerBound().getValueAs(PowerUnit.WATT);
            if (lowerBound < min) {
                min = lowerBound;
            }
        }

        return new PowerValue(min, PowerUnit.WATT);
    }

    /**
     * @return The maximum allowed power level according to the list of power constraints.
     */
    public PowerValue getMaximum() {
        double max = Double.MIN_VALUE;

        for (PowerConstraint constraint : this) {
            double upperBound = constraint.getUpperBound().getValueAs(PowerUnit.WATT);
            if (upperBound > max) {
                max = upperBound;
            }
        }

        return new PowerValue(max, PowerUnit.WATT);
    }
}
