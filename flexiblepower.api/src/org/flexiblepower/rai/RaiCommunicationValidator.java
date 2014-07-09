package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public interface RaiCommunicationValidator<A extends Allocation, RH extends ControlSpaceRegistration, RU extends ControlSpaceUpdate> {

    public void setResourceType(ResourceType<A, RH, RU> resourceType);

    public boolean validateResourceHandshake(RH resourceHandshake);

    public boolean validateResourceUpdate(RU resourceUpdate);

    public boolean validateAllocation(A allocation);

}
