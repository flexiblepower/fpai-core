package org.flexiblepower.rai;

import java.util.Date;

import org.flexiblepower.time.TimeService;

/**
 * An energy application can revoke sent {@link Allocation}s by sending the {@link AllocationRevoke} message. After
 * sending the message all the received {@link Allocation}s should be removed by the appliance driver. The appliance
 * driver should sent a new {@link ControlSpaceUpdate} message to get an {@link Allocation} to work with.
 */
public final class AllocationRevoke extends ResourceMessage {
    private static final long serialVersionUID = -5032317862969564414L;

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
