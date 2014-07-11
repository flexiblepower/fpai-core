package org.flexiblepower.rai.comm;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public abstract class ControlSpaceUpdate extends ResourceMessage {

    private static final long serialVersionUID = -242149664875591012L;

    private final Date validFrom;
    private final Measurable<Duration> allocationDelay;

    public ControlSpaceUpdate(String resourceId, Date timestamp, Date validFrom, Measurable<Duration> allocationDelay) {
        super(resourceId, timestamp);
        this.validFrom = validFrom;
        this.allocationDelay = allocationDelay;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public Measurable<Duration> getAllocationDelay() {
        return allocationDelay;
    }

}