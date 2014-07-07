package org.flexiblepower.rai.comm;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public abstract class ResourceInfo implements Serializable {

    private static final long serialVersionUID = -313146669543611880L;

    private final UUID resourceInfoId;
    private final String resourceId;
    private final Date timestamp;

    public ResourceInfo(String resourceId, Date timestamp) {
        super();
        resourceInfoId = UUID.randomUUID();
        this.resourceId = resourceId;
        this.timestamp = timestamp;
    }

    public UUID getResourceInfoId() {
        return resourceInfoId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
