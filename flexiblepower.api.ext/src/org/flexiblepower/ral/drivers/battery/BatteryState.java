package org.flexiblepower.ral.drivers.battery;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.flexiblepower.ral.ResourceState;

public interface BatteryState extends ResourceState {
    Measurable<Energy> getTotalCapacity();

    Measurable<Power> getChargeSpeed();

    Measurable<Power> getDischargeSpeed();

    Measurable<Power> getSelfDischargeSpeed();

    double getChargeEfficiency();

    double getDischargeEfficiency();

    Measurable<Duration> getMinimumOnTime();

    Measurable<Duration> getMinimumOffTime();

    double getStateOfCharge();

    BatteryMode getCurrentMode();
}
