package org.flexiblepower.rai.comm;

import java.util.Date;
import java.util.UUID;

public abstract class ResourceInfo {

    public final UUID resourceInfoId;
    public final String resourceId;
    public final Date timestamp;

    public ResourceInfo(String resourceId, Date timestamp) {
        super();
        resourceInfoId = UUID.randomUUID();
        this.resourceId = resourceId;
        this.timestamp = timestamp;
    }

    public UUID getResourceInfoId() {
        return resourceInfoId;
    }

}
