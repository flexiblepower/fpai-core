package org.flexiblepower.rai.comm;

import java.util.Date;
import java.util.UUID;

public abstract class Allocation extends ResourceInfo {

    private final UUID resourceUpdateId;

    public Allocation(String resourceId, ResourceUpdate resourceUpdate, Date timestamp) {
        super(resourceId, timestamp);
        resourceUpdateId = resourceUpdate.getResourceInfoId();
    }

}
