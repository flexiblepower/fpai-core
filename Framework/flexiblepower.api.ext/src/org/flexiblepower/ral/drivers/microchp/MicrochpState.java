package org.flexiblepower.ral.drivers.microchp;

import org.flexiblepower.ral.ResourceState;

public interface MicrochpState extends ResourceState {

    public static enum OperatingMode {
        OFF, AUTO, REDUCED, COMFORT
    }

    boolean boilerOn();

    double getRoomTemp();

    double getTargetTemp();

    double getKWhCummultive();

    OperatingMode getOperatingMode();

    double getCurrentError();
}
