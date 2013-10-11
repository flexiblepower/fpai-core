package org.flexiblepower.ral.drivers.battery;

import org.flexiblepower.ral.ResourceControlParameters;

/**
 * The control parameters for a battery is to set its mode.
 */
public interface BatteryControlParameters extends ResourceControlParameters {
    /**
     * @return The mode
     */
    BatteryMode getMode();
}
