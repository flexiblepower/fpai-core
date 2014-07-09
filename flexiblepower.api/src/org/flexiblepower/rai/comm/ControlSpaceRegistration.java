package org.flexiblepower.rai.comm;

import java.util.Date;

public abstract class ControlSpaceRegistration extends ResourceMessage {

    private static final long serialVersionUID = 8841022716486854027L;

    public ControlSpaceRegistration(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }

}
