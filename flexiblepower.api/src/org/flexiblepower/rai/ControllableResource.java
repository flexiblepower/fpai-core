package org.flexiblepower.rai;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceManager;

/**
 * The {@link ControllableResource} is the top interface of a {@link ResourceManager} that will be used for interacting
 * with the Energy App.
 * 
 * @see ControllerManager
 * @see Controller
 * @author TNO
 * @param <CS>
 *            The type of ControlSpace that will be produced by the resource.
 */
public interface ControllableResource<CS extends ControlSpace> {
    /**
     * Handles the allocation that should be created by a controller. The resource manager should translate this
     * allocation into actions that are executed using the connected driver.
     * 
     * @param allocation
     *            The allocation that is to be translated into actions. This allocation should never be null.
     * @throws NullPointerException
     *             When the allocation is null.
     * @throws IllegalStateException
     *             When there is no driver connected to this resource manager
     */
    void handleAllocation(Allocation allocation);

    /**
     * @return The type of resource that this manager represents.
     */
    Class<CS> getControlSpaceType();

    /**
     * Binds the controller of this resource. After this method has returned, all the control spaces should be sent to
     * this controller. This method should be call by the
     * {@link ControllerManager#registerResource(ControllableResource)} method.
     * 
     * Note: the runtime must take care that only one {@link Controller} is bound.
     * 
     * @param controller
     *            The {@link Controller} that will receive the control spaces.
     */
    void setController(Controller<? super CS> controller);

    /**
     * Unbinds the controller from this resource. After this method has returned, the controller will no longer receive
     * updates. This method should be call by the {@link ControllerManager#unregisterResource(ControllableResource)}
     * method.
     * 
     * @param controller
     *            The {@link Controller} that will no longer receive the control spaces.
     */
    void unsetController(Controller<? super CS> controller);
}
