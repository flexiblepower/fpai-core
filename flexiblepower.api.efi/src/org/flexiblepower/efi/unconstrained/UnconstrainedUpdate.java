package org.flexiblepower.efi.unconstrained;

import java.util.Date;

import org.flexiblepower.rai.ControlSpaceUpdate;
import org.flexiblepower.time.TimeService;

/**
 * This class is derived from {@link ControlSpaceUpdate} and is used to bundle all updates from an appliance of the
 * unconstrained category.
 */
public class UnconstrainedUpdate extends ControlSpaceUpdate {
    /**
     * Constructs a new {@link UnconstrainedUpdate} message with the specific validFrom
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     */
    public UnconstrainedUpdate(String resourceId, Date timestamp, Date validFrom) {
        super(resourceId, timestamp, validFrom);
    }
}
