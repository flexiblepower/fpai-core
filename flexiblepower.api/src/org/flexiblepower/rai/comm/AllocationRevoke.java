package org.flexiblepower.rai.comm;

import java.util.Date;

/**
 * 
 * An energy app can revoke sent allocations by sending the AllocationRevoke message. After sending the message all the
 * received Allocations should be removed by the appliance driver.
 * 
 * @author TNO
 * 
 */

public class AllocationRevoke extends ResourceMessage {

    private static final long serialVersionUID = -5032317862969564414L;

    public AllocationRevoke(String resourceId, Date timestamp) {
        super(resourceId, timestamp);
    }

}
