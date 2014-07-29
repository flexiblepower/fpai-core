package org.flexiblepower.rai.comm;

import java.util.Date;

public class AllocationRevoke extends ResourceMessage {

    private static final long serialVersionUID = -5032317862969564414L;

    public AllocationRevoke(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }

}
