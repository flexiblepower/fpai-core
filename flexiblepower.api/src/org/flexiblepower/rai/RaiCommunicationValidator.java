package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public interface RaiCommunicationValidator<A extends Allocation, CSR extends ControlSpaceRegistration, CSU extends ControlSpaceUpdate> {

    public void setResourceType(ResourceType<A, CSR, CSU> resourceType);

    public boolean validateResourceHandshake(CSR resourceHandshake);

    public boolean validateResourceUpdate(CSU resourceUpdate);

    public boolean validateAllocation(A allocation);

}
