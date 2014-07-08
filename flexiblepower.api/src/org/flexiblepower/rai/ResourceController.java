package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ResourceHandshake;
import org.flexiblepower.rai.comm.ResourceUpdate;

public interface ResourceController<RH extends ResourceHandshake, RU extends ResourceUpdate> {

    public void setResourceType(ResourceType<?, RH, RU> resourceType);

    public void handleResourceHandshake(RH resourceHandshake);

    public void handleResourceUpdate(RU resourceUpdate);

    public void handleAllocationStatusUpdate(AllocationStatusUpdate allocationStatusUpdate);

}
