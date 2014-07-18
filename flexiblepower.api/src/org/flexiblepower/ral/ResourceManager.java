package org.flexiblepower.ral;

import org.flexiblepower.observation.ObservationConsumer;
import org.flexiblepower.rai.ControllableResource;
import org.flexiblepower.rai.comm.Allocation;

/**
 * The {@link ResourceManager} is responsible to translating the current state of an appliance (as retrieved through a
 * {@link ResourceDriver}) to a {@link ControlSpace} and to translate the {@link Allocation}s to actions that are
 * performed on the {@link ResourceDriver}. The {@link ResourceDriver} is dynamically connected to the
 * {@link ResourceManager} in OSGi, by using matching 'resourceId' configuration parameter. This should be done by the
 * FPAI runtime environment.
 * 
 * @param <CS>
 *            The type of ControlSpace that will be produced.
 * @param <RS>
 *            The type of {@link ResourceState} that can be understood.
 * @param <RCP>
 *            The type of {@link ResourceControlParameters} that will we written to the driver.
 */
public interface ResourceManager<A extends Allocation, RS extends ResourceState, RCP extends ResourceControlParameters> extends
                                                                                                                        ObservationConsumer<RS>,
                                                                                                                        ControllableResource<A> {
    /**
     * Bind the given driver to this resource manager. The implementation of this method must make sure to call the
     * {@link ResourceDriver#subscribe(ObservationConsumer)} method with <code>this</code> as its parameter.
     * 
     * @param driver
     *            The driver that will be bound to this manager.
     */
    void registerDriver(ResourceDriver<? extends RS, ? super RCP> driver);

    /**
     * Unbinds the given driver from this resource manager. The implementation of this method must make sure to call the
     * {@link ResourceDriver#unsubscribe(ObservationConsumer)} method with <code>this</code> as its parameter.
     * 
     * @param driver
     *            The driver that will be unbound from this manager.
     */
    void unregisterDriver(ResourceDriver<? extends RS, ? super RCP> driver);
}
