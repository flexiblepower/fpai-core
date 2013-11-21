package org.flexiblepower.ral.drivers.refrigerator;

import javax.measure.Measurable;
import javax.measure.quantity.Temperature;

import org.flexiblepower.ral.ResourceState;

/**
 * The state of an refrigerator.
 */
public interface RefrigeratorState extends ResourceState {
    /**
     * @return The current temperature in the refrigerator.
     */
    Measurable<Temperature> getCurrentTemperature();

    /**
     * @return The target temperature in the refrigerator. The current temperature should always be lower than the
     *         target.
     */
    Measurable<Temperature> getTargetTemperature();

    /**
     * @return The minimum temperature in the refrigerator
     */
    Measurable<Temperature> getMinimumTemperature();

    /**
     * @return true when super cool is currently on, false if not.
     */
    boolean getSupercoolMode();
}
