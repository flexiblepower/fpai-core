package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public interface ResourceController<RH extends ControlSpaceRegistration, RU extends ControlSpaceUpdate> {

    public void setResourceType(ResourceType<?, RH, RU> resourceType);

    public void handleResourceHandshake(RH resourceHandshake);

    public void handleResourceUpdate(RU resourceUpdate);

    public void handleAllocationStatusUpdate(AllocationStatusUpdate allocationStatusUpdate);

}
