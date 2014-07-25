package org.flexiblepower.rai.comm;

import java.util.Date;
import java.util.UUID;

public abstract class Allocation extends ResourceMessage {

    private static final long serialVersionUID = 706199511692067676L;

    /** The id of the control space update on which this allocation message is based. */
    private final UUID controlSpaceUpdateId;

    /** This boolean indicates whether this is an emergency allocation, in which case it has to be strictly followed. */
    private final boolean isEmergencyAllocation;

    public Allocation(String resourceId,
                      ControlSpaceUpdate controlSpaceUpdate,
                      Date timestamp,
                      boolean isEmergencyAllocation) {
        super(resourceId, timestamp);
        controlSpaceUpdateId = controlSpaceUpdate.getResourceMessageId();
        this.isEmergencyAllocation = isEmergencyAllocation;
    }

    /**
     * Gets the id of the control space update on which this allocation message is based.
     * 
     * @return id of the control space update on which this allocation message is based.
     */
    public UUID getControlSpaceUpdateId() {
        return controlSpaceUpdateId;
    }

}
