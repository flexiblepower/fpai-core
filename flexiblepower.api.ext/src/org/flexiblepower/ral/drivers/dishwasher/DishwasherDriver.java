package org.flexiblepower.ral.drivers.dishwasher;

import org.flexiblepower.ral.ResourceDriver;

/**
 * A generic ResourceDriver for a dishwasher, using the {@link DishwasherState} and {@link DishwasherControlParameters}.
 */
public interface DishwasherDriver extends ResourceDriver<DishwasherState, DishwasherControlParameters> {

}
