package org.flexiblepower.ral.messages;

import java.util.Date;

/**
 * An energy application can revoke sent {@link Allocation}s by sending the {@link AllocationRevoke} message. After
 * sending the message all the received {@link Allocation}s should be removed by the appliance driver. The appliance
 * driver should sent a new {@link ControlSpaceUpdate} message to get an {@link Allocation} to work with.
 */
public final class AllocationRevoke extends ResourceMessage {
    /**
     * Creates a new {@link AllocationRevoke} message for a specific resource.
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     */
    public AllocationRevoke(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }
}
