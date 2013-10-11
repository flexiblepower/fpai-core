package org.flexiblepower.ral.drivers.battery;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.flexiblepower.ral.ResourceState;

/**
 * The state of the battery.
 */
public interface BatteryState extends ResourceState {
    /**
     * @return The current total capacity
     */
    Measurable<Energy> getTotalCapacity();

    /**
     * @return The power with which it charges.
     */
    Measurable<Power> getChargeSpeed();

    /**
     * @return The power with which it discharges.
     */
    Measurable<Power> getDischargeSpeed();

    /**
     * @return The amount of power that it leaks.
     */
    Measurable<Power> getSelfDischargeSpeed();

    /**
     * @return The efficiency of charging.
     */
    double getChargeEfficiency();

    /**
     * @return The efficiency of discharging.
     */
    double getDischargeEfficiency();

    /**
     * @return The minimum time during which it should be on.
     */
    Measurable<Duration> getMinimumOnTime();

    /**
     * @return The minimum time during which it should be off.
     */
    Measurable<Duration> getMinimumOffTime();

    /**
     * @return The current state of charge [0,1].
     */
    double getStateOfCharge();

    /**
     * @return The current mode.
     */
    BatteryMode getCurrentMode();
}
