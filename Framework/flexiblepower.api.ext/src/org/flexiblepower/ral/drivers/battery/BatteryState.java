package org.flexiblepower.ral.drivers.battery;

import org.flexiblepower.rai.values.Duration;
import org.flexiblepower.rai.values.EnergyValue;
import org.flexiblepower.rai.values.PowerValue;
import org.flexiblepower.ral.ResourceState;

public interface BatteryState extends ResourceState {
    EnergyValue getTotalCapacity();

    PowerValue getChargeSpeed();

    PowerValue getDischargeSpeed();

    PowerValue getSelfDischargeSpeed();

    double getChargeEfficiency();

    double getDischargeEfficiency();

    Duration getMinimumOnTime();

    Duration getMinimumOffTime();

    double getStateOfCharge();

    BatteryMode getCurrentMode();
}
