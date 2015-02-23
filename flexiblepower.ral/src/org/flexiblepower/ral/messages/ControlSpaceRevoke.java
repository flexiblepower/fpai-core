package org.flexiblepower.ral.messages;

import java.util.Date;

/**
 * An appliance driver can revoke an already sent {@link ControlSpaceUpdate} message by sending the
 * {@link ControlSpaceRevoke} message. After sending the message every received {@link ControlSpaceUpdate} should be
 * removed by the energy app, only the registration message is valid afterwards.
 */
public class ControlSpaceRevoke extends ResourceMessage {
    /**
     * Creates a new {@link ControlSpaceRevoke} message for a specific resource.
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called
     */
    public ControlSpaceRevoke(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }
}
