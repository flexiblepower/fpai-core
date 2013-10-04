package org.flexiblepower.ral;

import org.flexiblepower.observation.ObservationConsumer;
import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.ControlSpace;
import org.flexiblepower.rai.ControllableResource;

/**
 * The {@link ResourceManager} is responsible to translating the current state of an appliance (as retrieved through a
 * {@link ResourceDriver}) to a {@link ControlSpace} and to translate the {@link Allocation}s to actions that are
 * performed on the {@link ResourceDriver}. The {@link ResourceDriver} is dynamically connected to the
 * {@link ResourceManager} in OSGi, by using matching 'appliance.id' configuration parameter. This should be done by the
 * FPAI runtime environment.
 */
public interface ResourceManager<CS extends ControlSpace, RS extends ResourceState, RCP extends ResourceControlParameters> extends
                                                                                                                           ObservationConsumer<RS>,
                                                                                                                           ControllableResource<CS> {
    void registerDriver(ResourceDriver<RS, RCP> driver);

    void unregisterDriver(ResourceDriver<RS, RCP> driver);
}
