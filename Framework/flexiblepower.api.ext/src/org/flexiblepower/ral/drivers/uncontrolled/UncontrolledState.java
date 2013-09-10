package org.flexiblepower.ral.drivers.uncontrolled;

import java.util.Date;

import org.flexiblepower.rai.values.PowerValue;
import org.flexiblepower.ral.ResourceState;

public interface UncontrolledState extends ResourceState {
    PowerValue getDemand();

    Date getTime();
}
