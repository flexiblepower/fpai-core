package org.flexiblepower.rai.comm;

import java.util.Date;
import java.util.UUID;

public abstract class Allocation extends ResourceMessage {

    private static final long serialVersionUID = 706199511692067676L;

    /** The id of the control space update on which this allocation message is based. */
    private final UUID controlSpaceUpdateId;

    public Allocation(String resourceId, ControlSpaceUpdate controlSpaceUpdate, Date timestamp) {
        super(resourceId, timestamp);
        controlSpaceUpdateId = controlSpaceUpdate.getResourceMessageId();
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
