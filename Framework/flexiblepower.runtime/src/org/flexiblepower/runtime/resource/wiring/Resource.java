package org.flexiblepower.runtime.resource.wiring;

import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;

public interface Resource<RS extends ResourceState> {
    String getId();

    ControllerManager getController();

    Set<ResourceManager<RS>> getResourceManagers();

    Set<ResourceDriver<RS, ?>> getResourceDrivers();
}
