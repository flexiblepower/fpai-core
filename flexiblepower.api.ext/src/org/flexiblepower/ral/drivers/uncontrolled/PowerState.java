package org.flexiblepower.ral.drivers.uncontrolled;

import javax.measure.Measurable;
import javax.measure.quantity.Power;

import org.flexiblepower.ral.ResourceState;

public interface PowerState extends ResourceState {
    /**
     * @return The current power consumption (or production if negative).
     */
    Measurable<Power> getCurrentUsage();
}
