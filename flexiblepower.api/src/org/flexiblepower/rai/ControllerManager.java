package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;

public interface ControllerManager<A extends Allocation> {

    public void setResourceType(ResourceType<A, ?, ?> resourceType);

    void registerResource(ControllableResource<A> resource);

    void unregisterResource(ControllableResource<A> resource);
}
