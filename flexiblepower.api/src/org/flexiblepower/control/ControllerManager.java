package org.flexiblepower.control;

import org.flexiblepower.rai.ControllableResource;
import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceType;
import org.flexiblepower.rai.old.ControlSpace;
import org.flexiblepower.rai.old.Controller;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;

/**
 * The {@link ControllerManager} is the main interface of an Energy App. There are two functions that have to do with
 * the lifecycle of {@link Controller}s. The runtime implementation of the framework will call the
 * {@link #registerResource(ControllableResource)} method for each {@link ControllableResource} (usually a
 * {@link ResourceManager} that is linked to this manager. The implementation of the {@link ControllerManager} must then
 * couple a {@link Controller} to the resource by using its {@link ControllableResource#setController(Controller)}
 * method.
 * 
 * There are 2 typical implementations:
 * 
 * 1) When there is one {@link Controller} for all the resources. This can be implemented easily by creating a class
 * that implements both this interface and the {@link Controller} interface. Then for each resource that is registered,
 * it simply binds itself to the resource.
 * 
 * 2) When there is on {@link Controller} for each resource. In this case there are probably several {@link Controller}
 * implementations (e.g. for each {@link ControlSpace} type) and for each resource a new instance is created for its
 * management.
 * 
 * Coupling of resources to the {@link ControllerManager} is done by the "resourceIds" property in the service registry.
 * This property should contain a list of resource identifiers that must match with the "resourecId" property of
 * {@link ResourceManager}s and {@link ResourceDriver}s.
 * 
 * @author TNO
 */
public interface ControllerManager {
    /**
     * This method is called by the runtime when a resource is bound to this manager. The implementation must always
     * call the {@link ControllableResource#setController(Controller)} method to bind a {@link Controller} to the
     * resource.
     * 
     * @param resource
     *            The resource that will be controlled by this Energy App.
     */
    ResourceController<?, ?> registerResource(ControllableResource<?> resource, ResourceType<?, ?, ?> resourceType);

    /**
     * This method is called by the runtime when a resource is unbound from this manager. The implementation must always
     * call the {@link ControllableResource#unsetController(Controller)} method to unbind the {@link Controller} from
     * the resource.
     * 
     * @param resource
     *            The resource that will no longer be controlled by the Energy App.
     */
    void unregisterResource(ControllableResource<?> resource);
}
