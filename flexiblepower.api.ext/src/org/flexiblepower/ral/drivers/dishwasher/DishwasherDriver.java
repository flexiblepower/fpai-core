package org.flexiblepower.ral.drivers.dishwasher;

import org.flexiblepower.messaging.Port;
import org.flexiblepower.ral.ResourceDriver;

/**
 * A generic ResourceDriver for a dishwasher, using the {@link DishwasherState} and {@link DishwasherControlParameters}.
 */
@Port(name = "manager", sends = DishwasherState.class, accepts = DishwasherControlParameters.class)
public interface DishwasherDriver extends ResourceDriver {
}
