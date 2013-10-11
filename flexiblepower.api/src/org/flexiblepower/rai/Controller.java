package org.flexiblepower.rai;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceManager;

/**
 * A {@link Controller} is part of the Energy App that will actually receive the {@link ControlSpace}s from the
 * {@link ControllableResource} (typically a {@link ResourceManager}).
 * 
 * @see ControllerManager
 * 
 * @author TNO
 * @param <CS>
 *            The type of {@link ControlSpace} the it can receive.
 */
public interface Controller<CS extends ControlSpace> {
    /**
     * This method should be called by the {@link ControllableResource} to update its {@link ControlSpace}.
     * 
     * @param resource
     *            The resource that sents the control space.
     * @param controlSpace
     *            The control space that describes the flexibility of the resource.
     */
    void controlSpaceUpdated(ControllableResource<? extends CS> resource, CS controlSpace);
}
