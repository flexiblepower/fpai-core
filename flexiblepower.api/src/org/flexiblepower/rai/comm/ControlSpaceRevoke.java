package org.flexiblepower.rai.comm;

import java.util.Date;

public class ControlSpaceRevoke extends ResourceMessage {

    private static final long serialVersionUID = -7711292648789098417L;

    public ControlSpaceRevoke(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }

}
