package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.ral.messages.ControlSpaceUpdate;

/**
 * A marker class for uncontrolled updates. Parent of {@link UncontrolledMeasurement} and {@link UncontrolledForecast}.
 */
public abstract class UncontrolledUpdate extends ControlSpaceUpdate {
    /**
     * Constructs a new {@link UncontrolledUpdate} message with the specific validFrom
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     */
    public UncontrolledUpdate(String resourceId, Date timestamp, Date validFrom) {
        super(resourceId, timestamp, validFrom);
    }
}
