package org.flexiblepower.rai.comm;

import java.util.Date;
import java.util.UUID;

public abstract class Allocation extends ResourceMessage {

    private static final long serialVersionUID = 706199511692067676L;

    private final UUID resourceUpdateId;

    public Allocation(String resourceId, ControlSpaceUpdate resourceUpdate, Date timestamp) {
        super(resourceId, timestamp);
        resourceUpdateId = resourceUpdate.getResourceInfoId();
    }

    public UUID getResourceUpdateId() {
        return resourceUpdateId;
    }

}
