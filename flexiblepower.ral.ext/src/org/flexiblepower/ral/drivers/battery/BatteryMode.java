package org.flexiblepower.ral.drivers.battery;

/**
 * To represent the different modes a battery can be in.
 */
public enum BatteryMode {
    /**
     * When the battery is idle (no interaction with the net).
     */
    IDLE,
    /**
     * When the battery is charging (taking energy from the net).
     */
    CHARGE,
    /**
     * When the battery is discharging (providing energy to the net).
     */
    DISCHARGE;
}
