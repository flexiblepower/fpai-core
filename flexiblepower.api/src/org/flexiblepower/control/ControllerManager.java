package org.flexiblepower.control;

import org.flexiblepower.rai.ControllableResource;

public interface ControllerManager {
    void registerResource(ControllableResource<?> resource);

    void unregisterResource(ControllableResource<?> resource);
}
