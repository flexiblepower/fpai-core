package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.AllocationRevoke;

public interface ControllableResource<A extends Allocation> {

    public ResourceType<A, ?, ?> getResourceType();

    public void initialize(ResourceMessageSubmitter resourceMessageSubmitter);

    public void handleAllocation(A allocation);

    public void handleAllocationRevoke(AllocationRevoke allocationRevoke);

    public void disconnect();

}
