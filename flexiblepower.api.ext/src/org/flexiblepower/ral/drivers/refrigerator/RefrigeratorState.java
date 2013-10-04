package org.flexiblepower.ral.drivers.refrigerator;

import org.flexiblepower.ral.ResourceState;

public interface RefrigeratorState extends ResourceState {
    double getCurrentTemperature();

    double getTargetTemperature();

    double getMinimumTemperature();

    boolean getSupercoolMode();
}
