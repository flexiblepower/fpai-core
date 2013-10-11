package org.flexiblepower.ral.drivers.refrigerator;

import org.flexiblepower.ral.ResourceControlParameters;

/**
 * The control, setting the super cool on or off.
 */
public interface RefrigeratorControlParameters extends ResourceControlParameters {
    /**
     * @return true when the super cool mode should be on, false if not.
     */
    boolean getSupercoolMode();
}
