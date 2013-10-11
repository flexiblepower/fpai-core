package org.flexiblepower.ral.drivers.uncontrolled;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Power;

import org.flexiblepower.ral.ResourceState;

/**
 * The state given the current demand for power.
 */
public interface UncontrolledState extends ResourceState {
    /**
     * @return The current demand.
     */
    Measurable<Power> getDemand();

    /**
     * @return The timestamp at which the demand has been measured.
     */
    Date getTime();
}
