package org.flexiblepower.rai.comm;

import java.util.Date;
import java.util.UUID;

public final class AllocationStatusUpdate extends ResourceMessage {

    private static final long serialVersionUID = 9119759551623204007L;

    public enum AllocationStatus {
        ACCEPTED, REJECTED, PROCESSING, STARTED, FINISHED
    };

    private final UUID allocationId;
    private final AllocationStatus status;
    private final String additionalInfo;

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

    public UUID getAllocationId() {
        return allocationId;
    }

    public AllocationStatus getStatus() {
        return status;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

}
