package org.flexiblepower.rai;

import java.util.Date;

import org.flexiblepower.time.TimeService;

/**
 * An appliance driver can revoke an already sent {@link ControlSpaceUpdate} message by sending the
 * {@link ControlSpaceRevoke} message. After sending the message every received {@link ControlSpaceUpdate} should be
 * removed by the energy app, only the registration message is valid afterwards.
 */
public class ControlSpaceRevoke extends ResourceMessage {
    private static final long serialVersionUID = -7711292648789098417L;

    /**
     * Creates a new {@link ControlSpaceRevoke} message for a specific resource.
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     */
    public ControlSpaceRevoke(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }
}
