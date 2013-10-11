package org.flexiblepower.ral.drivers.battery;

import org.flexiblepower.ral.ResourceDriver;

/**
 * The generic {@link BatteryDriver} that uses the {@link BatteryState} and {@link BatteryControlParameters}.
 */
public interface BatteryDriver extends ResourceDriver<BatteryState, BatteryControlParameters> {
}
