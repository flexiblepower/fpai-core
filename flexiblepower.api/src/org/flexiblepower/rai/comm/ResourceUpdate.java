package org.flexiblepower.rai.comm;

import java.util.Date;

public abstract class ResourceUpdate extends ResourceInfo {

    private static final long serialVersionUID = -242149664875591012L;

    public ResourceUpdate(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }

}
