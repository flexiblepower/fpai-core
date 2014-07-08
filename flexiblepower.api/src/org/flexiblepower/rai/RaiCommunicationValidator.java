package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ResourceHandshake;
import org.flexiblepower.rai.comm.ResourceUpdate;

public interface RaiCommunicationValidator<A extends Allocation, RH extends ResourceHandshake, RU extends ResourceUpdate> {

    public void setResourceType(ResourceType<A, RH, RU> resourceType);

    public boolean validateResourceHandshake(RH resourceHandshake);

    public boolean validateResourceUpdate(RU resourceUpdate);

    public boolean validateAllocation(A allocation);

}
