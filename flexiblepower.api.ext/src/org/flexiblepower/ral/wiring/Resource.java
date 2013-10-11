package org.flexiblepower.ral.wiring;

import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;

/**
 * A Resource is a representation of a combination of {@link ResourceDriver}, {@link ResourceManager} and
 * {@link ControllerManager} that are bound together.
 * 
 * @param <RS>
 *            The type of {@link ResourceState}
 * @param <RCP>
 *            The type of {@link ResourceControlParameters}
 */
public interface Resource<RS extends ResourceState, RCP extends ResourceControlParameters> {
    /**
     * @return The resource identifier.
     */
    String getId();

    /**
     * @return The {@link ControllerManager} that is responsible for creating the Controller for the
     *         {@link ResourceManager}.
     */
    ControllerManager getControllerManager();

    /**
     * @return The {@link ResourceManager}s that have the same identifier.
     */
    Set<ResourceManager<?, RS, RCP>> getResourceManagers();

    /**
     * @return The {@link ResourceDriver} that have the same identifier.
     */
    Set<ResourceDriver<RS, RCP>> getResourceDrivers();
}
