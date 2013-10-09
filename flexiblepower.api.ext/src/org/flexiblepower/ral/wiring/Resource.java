package org.flexiblepower.ral.wiring;

import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;

public interface Resource<RS extends ResourceState, RCP extends ResourceControlParameters> {
    String getId();

    ControllerManager getControllerManager();

    Set<ResourceManager<?, RS, RCP>> getResourceManagers();

    Set<ResourceDriver<RS, RCP>> getResourceDrivers();
}
