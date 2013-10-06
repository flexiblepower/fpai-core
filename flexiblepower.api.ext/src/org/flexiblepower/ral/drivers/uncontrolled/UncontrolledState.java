package org.flexiblepower.ral.drivers.uncontrolled;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Power;

import org.flexiblepower.ral.ResourceState;

public interface UncontrolledState extends ResourceState {
    Measurable<Power> getDemand();

    Date getTime();
}
