package org.flexiblepower.rai;

import java.util.Date;
import java.util.UUID;

import org.flexiblepower.time.TimeService;

/**
 * After an appliance driver received an {@link Allocation} message, it is good practice to provide feedback to the
 * energy application on the follow up actions. This can be done via this message. All ControlSpace categories use this
 * {@link AllocationStatusUpdate} message; there are no specific derivations in use.
 */
public final class AllocationStatusUpdate extends ResourceMessage {
    private static final long serialVersionUID = 9119759551623204007L;

    private final UUID allocationId;
    private final AllocationStatus status;
    private final String additionalInfo;

    /**
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     *
     * @param allocation
     *            the {@link Allocation} to which this update is the response
     * @param status
     *            This attribute indicates the current status of the {@link Allocation}. It assumes one of the values
     *            specified in the {@link AllocationStatus} enumeration.
     * @param additionalInfo
     *            In addition to the status attribute additional and more specific information can be expressed in a
     *            human readable format.
     */
    public AllocationStatusUpdate(Date timestamp, Allocation allocation, AllocationStatus status, String additionalInfo) {
        this(allocation.getResourceId(), timestamp, allocation.getResourceMessageId(), status, additionalInfo);
    }

    /**
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param allocationId
     *            An identifier that uniquely identifies the {@link Allocation} message that this update refers to (the
     *            {@link Allocation#getResourceMessageId()}).
     * @param status
     *            This attribute indicates the current status of the {@link Allocation}. It assumes one of the values
     *            specified in the {@link AllocationStatus} enumeration.
     * @param additionalInfo
     *            In addition to the status attribute additional and more specific information can be expressed in a
     *            human readable format.
     */
    public AllocationStatusUpdate(String resourceId,
                                  Date timestamp,
                                  UUID allocationId,
                                  AllocationStatus status,
                                  String additionalInfo) {
        super(resourceId, timestamp);
        this.allocationId = allocationId;
        this.status = status;
        this.additionalInfo = additionalInfo;
    }

    /**
     * @return An identifier that uniquely identifies the {@link Allocation} message that this update refers to (the
     *         {@link Allocation#getResourceMessageId()}).
     */
    public UUID getAllocationId() {
        return allocationId;
    }

    /**
     * @return This attribute indicates the current status of the {@link Allocation}. It assumes one of the values
     *         specified in the {@link AllocationStatus} enumeration.
     */
    public AllocationStatus getStatus() {
        return status;
    }

    /**
     * @return In addition to the status attribute additional and more specific information can be expressed in a human
     *         readable format.
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((additionalInfo == null) ? 0 : additionalInfo.hashCode());
        result = prime * result + ((allocationId == null) ? 0 : allocationId.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        AllocationStatusUpdate other = (AllocationStatusUpdate) obj;
        if (additionalInfo == null) {
            if (other.additionalInfo != null) {
                return false;
            }
        } else if (!additionalInfo.equals(other.additionalInfo)) {
            return false;
        }
        if (allocationId == null) {
            if (other.allocationId != null) {
                return false;
            }
        } else if (!allocationId.equals(other.allocationId)) {
            return false;
        }
        if (status != other.status) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append("allocationId=").append(allocationId).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("additionalInfo=").append(additionalInfo).append(", ");
    }
}
