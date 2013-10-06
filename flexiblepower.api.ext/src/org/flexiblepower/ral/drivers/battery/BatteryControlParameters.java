package org.flexiblepower.ral.drivers.battery;

import org.flexiblepower.ral.ResourceControlParameters;

public interface BatteryControlParameters extends ResourceControlParameters {
    BatteryMode getMode();
}
