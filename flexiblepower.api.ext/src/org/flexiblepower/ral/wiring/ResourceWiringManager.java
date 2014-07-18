package org.flexiblepower.ral.wiring;

import java.util.Collection;

import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;

/**
 * A {@link ResourceWiringManager} is responsible for coupling {@link ResourceDriver}, {@link ResourceManager} and
 * {@link Controller} types together using the resourceId properties in the service repository.
 */
public interface ResourceWiringManager {
    final String RESOURCE_ID = "resourceId";
    final String RESOURCE_IDS = "resourceIds";

    /**
     * @return A collection with all the resources that are currently active.
     */
    Collection<Resource<?, ?>> getResources();

    /**
     * @return The number of active resources.
     */
    int size();
}
