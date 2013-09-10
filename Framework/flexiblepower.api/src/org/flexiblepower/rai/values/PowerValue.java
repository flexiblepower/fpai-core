package org.flexiblepower.rai.values;

import org.flexiblepower.rai.unit.PowerUnit;

/**
 * Power representing real power.
 * 
 * PMSuite - PM Data Specification - v0.6
 */
public class PowerValue extends Value<PowerUnit> {
    public PowerValue(double value, PowerUnit unitPower) {
        super(value, unitPower);
    }

    @Override
    public double getValueAsDefaultUnit() {
        return getValueAs(PowerUnit.WATT);
    }
}
