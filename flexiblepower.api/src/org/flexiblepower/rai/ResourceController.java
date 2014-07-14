package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public interface ResourceController<CSR extends ControlSpaceRegistration, RU extends ControlSpaceUpdate> {

    public void initialize(ResourceType<?, CSR, RU> resourceType,
                           CSR controlSpaceRegistration,
                           ResourceMessageSubmitter resourceMessageSubmitter);

    public void handleResourceUpdate(RU resourceUpdate);

    public void handleAllocationStatusUpdate(AllocationStatusUpdate allocationStatusUpdate);

}
