package org.flexiblepower.ral.drivers.battery;

import org.flexiblepower.messaging.Port;
import org.flexiblepower.ral.ResourceDriver;

/**
 * The generic {@link BatteryDriver} that uses the {@link BatteryState} and {@link BatteryControlParameters}.
 */
@Port(name = "manager", sends = BatteryState.class, accepts = BatteryControlParameters.class)
public interface BatteryDriver extends ResourceDriver {
}
