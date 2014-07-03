package org.flexiblepower.rai.comm;

import java.util.Date;

public abstract class ResourceUpdate extends ResourceInfo {

    public ResourceUpdate(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }

}
