package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceRevoke;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public interface ResourceController<CSR extends ControlSpaceRegistration, CSU extends ControlSpaceUpdate> {

    public void initialize(CSR controlSpaceRegistration, ResourceMessageSubmitter resourceMessageSubmitter);

    public void handleResourceUpdate(CSU resourceUpdate);

    public void handleAllocationStatusUpdate(AllocationStatusUpdate allocationStatusUpdate);

    public void handleControlSpaceRevoke(ControlSpaceRevoke controlSpaceRevoke);
}
