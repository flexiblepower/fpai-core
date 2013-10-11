package org.flexiblepower.ral.drivers.refrigerator;

import org.flexiblepower.ral.ResourceState;

/**
 * The state of an refrigerator.
 */
public interface RefrigeratorState extends ResourceState {
    /**
     * @return The current temperature in the refrigerator in degrees Celcius.
     */
    double getCurrentTemperature();

    /**
     * @return The target temperature in the refrigerator in degrees Celcius. The current temperature should always be
     *         lower than the target.
     */
    double getTargetTemperature();

    /**
     * @return The minimum temperature in the refrigerator in degrees Celcius.
     */
    double getMinimumTemperature();

    /**
     * @return true when super cool is currently on, false if not.
     */
    boolean getSupercoolMode();
}
