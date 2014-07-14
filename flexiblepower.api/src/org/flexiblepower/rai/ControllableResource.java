package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;

public interface ControllableResource<A extends Allocation> {

    public ResourceType<A, ?, ?> getResourceType();

    public void initialize(ResourceMessageSubmitter resourceMessageSubmitter);

    public void handleAllocation(A allocation);

}
