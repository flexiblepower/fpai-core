package org.flexiblepower.rai;

public interface ControllerManager {

    void registerResource(ControllableResource<?> resource, ResourceType<?, ?, ?> resourceType);

    void unregisterResource(ControllableResource<?> resource, ResourceType<?, ?, ?> resourceType);
}
