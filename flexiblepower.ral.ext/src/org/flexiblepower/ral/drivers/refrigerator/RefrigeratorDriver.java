package org.flexiblepower.ral.drivers.refrigerator;

import org.flexiblepower.messaging.Port;
import org.flexiblepower.ral.ResourceDriver;

/**
 * The {@link ResourceDriver} to represent a refrigerator, using the {@link RefrigeratorState} and
 * {@link RefrigeratorControlParameters}.
 */
@Port(name = "manager", sends = RefrigeratorState.class, accepts = RefrigeratorControlParameters.class)
public interface RefrigeratorDriver extends ResourceDriver {
}
