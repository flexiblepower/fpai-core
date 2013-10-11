package org.flexiblepower.ral.drivers.dishwasher;

import org.flexiblepower.ral.ResourceControlParameters;

/**
 * The control of the dishwasher, a simple boolean value.
 */
public interface DishwasherControlParameters extends ResourceControlParameters {
    /**
     * @return true when the current selected program should be started.
     */
    boolean getStartProgram();
}
