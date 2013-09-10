package org.flexiblepower.rai.values;

import org.flexiblepower.rai.unit.EnergyUnit;
import org.flexiblepower.rai.unit.PowerUnit;
import org.flexiblepower.rai.unit.TimeUnit;

/**
 * Energy representing real energy.
 * 
 * PMSuite - PM Data Specification - v0.6
 */
public class EnergyValue extends Value<EnergyUnit> {
    public EnergyValue(double value, EnergyUnit unitEnergy) {
        super(value, unitEnergy);
    }

    public PowerValue getAveragePower(Duration duration) {
        return new PowerValue(getValueAs(EnergyUnit.JOULE) / duration.getValueAs(TimeUnit.SECONDS), PowerUnit.WATT);
    }

    @Override
    public double getValueAsDefaultUnit() {
        return getValueAs(EnergyUnit.JOULE);
    }
}
