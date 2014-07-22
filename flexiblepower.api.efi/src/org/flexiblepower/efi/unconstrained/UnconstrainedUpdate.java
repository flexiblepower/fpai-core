package org.flexiblepower.efi.unconstrained;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public class UnconstrainedUpdate extends ControlSpaceUpdate {

    private static final long serialVersionUID = 3798980923200359354L;

    public UnconstrainedUpdate(String resourceId, Date timestamp, Date validFrom, Measurable<Duration> allocationDelay) {
        super(resourceId, timestamp, validFrom, allocationDelay);
    }

}
