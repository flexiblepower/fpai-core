package org.flexiblepower.ral.drivers.uncontrolled;

import org.flexiblepower.messaging.Port;
import org.flexiblepower.ral.ResourceDriver;

/**
 * The {@link ResourceDriver} to represent an generic uncontrolled machine, using the {@link PowerState}.
 */
@Port(name = "manager", sends = PowerState.class)
public interface UncontrollableDriver extends ResourceDriver {
}
