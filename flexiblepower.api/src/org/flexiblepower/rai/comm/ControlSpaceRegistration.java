package org.flexiblepower.rai.comm;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

/**
 * The control space registration class is a parent class for the BufferRegistration, TimeShifterRegistration,
 * UncontrolledRegistration and UnconstrainedRegstration class.
 * 
 * @author TNO
 * 
 */

public abstract class ControlSpaceRegistration extends ResourceMessage {

    private static final long serialVersionUID = 8841022716486854027L;

    /**
     * The duration of the delay in communications channel from the moment of sending to the moment the command is
     * executed up by the device.
     */
    private final Measurable<Duration> allocationDelay;

    public ControlSpaceRegistration(String resourceId, Date timestamp, Measurable<Duration> allocationDelay) {
        super(resourceId, timestamp);
        this.allocationDelay = allocationDelay;
    }

    public Measurable<Duration> getAllocationDelay() {
        return allocationDelay;
    }

}
