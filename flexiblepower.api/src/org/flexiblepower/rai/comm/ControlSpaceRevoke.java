package org.flexiblepower.rai.comm;

import java.util.Date;

/**
 * The appliance driver can revoke control space updates by sending the ControleSpaceRevoke message. After sending the
 * message every sent ControlSpaceUpdate should be removed by the energy app, only the registration message is valid
 * afterwards.
 * 
 * @author TNO
 * 
 */

public class ControlSpaceRevoke extends ResourceMessage {

    private static final long serialVersionUID = -7711292648789098417L;

    public ControlSpaceRevoke(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }

}
