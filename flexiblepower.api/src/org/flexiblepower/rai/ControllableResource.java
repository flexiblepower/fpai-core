package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;

public interface ControllableResource<A extends Allocation> {

    public void initialize(ResourceType<A, ?, ?> resourceType);

    public void handleAllocation(A allocation);

}
