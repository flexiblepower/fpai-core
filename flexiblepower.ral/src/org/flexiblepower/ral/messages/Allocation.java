package org.flexiblepower.ral.messages;

import java.util.Date;
import java.util.UUID;

/**
 * An {@link Allocation} message is always a response from the energy application to a {@link ControlSpaceUpdate}
 * message. It contains instructions on how to use (or fix) the flexibility described in the {@link ControlSpaceUpdate}.
 *
 * Each ControlSpace category has its own {@link Allocation} message that is derived from this one.
 */
public abstract class Allocation extends ResourceMessage {
    private final UUID controlSpaceUpdateId;
    private final boolean isEmergencyAllocation;

    /**
     * Constructs a new {@link Allocation} object as a response to a specific {@link ControlSpaceUpdate} which is no
     * emergency. This object will return <code>false</code> for the {@link #isEmergencyAllocation()} method.
     *
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param controlSpaceUpdate
     *            The {@link ControlSpaceUpdate} object to which this {@link Allocation} is responding to.
     */
    public Allocation(Date timestamp,
                      ControlSpaceUpdate controlSpaceUpdate) {
        this(timestamp, controlSpaceUpdate, false);
    }

    /**
     * Constructs a new {@link Allocation} object as a response to a specific {@link ControlSpaceUpdate}.
     *
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param controlSpaceUpdate
     *            The {@link ControlSpaceUpdate} object to which this {@link Allocation} is responding to.
     * @param isEmergencyAllocation
     *            This Boolean value is optional and is true when a grid emergency situation occurs. (e.g. congestion,
     *            black start etc.) The energy app then strongly advices the appliance driver to adapt to the sent
     *            allocation in order to maintain grid stability.
     */
    public Allocation(Date timestamp,
                      ControlSpaceUpdate controlSpaceUpdate,
                      boolean isEmergencyAllocation) {
        this(controlSpaceUpdate.getResourceId(),
             timestamp,
             controlSpaceUpdate.getResourceMessageId(),
             isEmergencyAllocation);
    }

    /**
     * Constructs a new {@link Allocation} object.
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param controlSpaceUpdateId
     *            An identifier that uniquely identifies the {@link ControlSpaceUpdate} message that this message is a
     *            response to.
     * @param isEmergencyAllocation
     *            This Boolean value is optional and is true when a grid emergency situation occurs. (e.g. congestion,
     *            black start etc.) The energy app then strongly advices the appliance driver to adapt to the sent
     *            allocation in order to maintain grid stability.
     */
    public Allocation(String resourceId, Date timestamp, UUID controlSpaceUpdateId, boolean isEmergencyAllocation) {
        super(resourceId, timestamp);
        if (controlSpaceUpdateId == null) {
            throw new NullPointerException("controlSpaceUpdateId");
        }

        this.controlSpaceUpdateId = controlSpaceUpdateId;
        this.isEmergencyAllocation = isEmergencyAllocation;
    }

    /**
     * @return The id of the control space update on which this allocation message is based.
     */
    public UUID getControlSpaceUpdateId() {
        return controlSpaceUpdateId;
    }

    /**
     * @return This boolean value is optional and is true when a grid emergency situation occurs. (e.g. congestion,
     *         black start etc.) The energy application then strongly advices the appliance driver to adapt to the sent
     *         allocation in order to maintain grid stability.
     */
    public boolean isEmergencyAllocation() {
        return isEmergencyAllocation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + controlSpaceUpdateId.hashCode();
        result = prime * result + (isEmergencyAllocation ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        Allocation other = (Allocation) obj;
        return controlSpaceUpdateId.equals(other.controlSpaceUpdateId) && isEmergencyAllocation == other.isEmergencyAllocation;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("controlSpaceUpdatedId=").append(controlSpaceUpdateId).append(", ");
        sb.append("isEmergencyAllocation=").append(isEmergencyAllocation).append(", ");
    }
}
