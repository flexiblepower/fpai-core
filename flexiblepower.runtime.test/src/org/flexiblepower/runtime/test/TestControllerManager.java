package org.flexiblepower.runtime.test;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestControllerManager extends IdentifyableObject implements ControllerManager {
    private static final Logger logger = LoggerFactory.getLogger(TestControllerManager.class);

    // @Override
    // public void registerResource(ControllableResource<?> resource) {
    // logger.trace("registerResource({})", resource);
    // resource.setController(this);
    // }
    //
    // @Override
    // public void unregisterResource(ControllableResource<?> resource) {
    // logger.trace("unregisterResource({})", resource);
    // resource.unsetController(this);
    // }
    //
    // @Override
    // public void controlSpaceUpdated(ControllableResource<? extends ControlSpace> resource, ControlSpace controlSpace)
    // {
    // logger.trace("controlSpaceUpdated({}, {})", resource, controlSpace);
    // }

    @Override
    public ResourceController<?, ?> registerResource(org.flexiblepower.rai.ControllableResource<?> resource,
                                                     ResourceType<?, ?, ?> resourceType) {
        logger.trace("registerResource({})", resource);
        // TODO Auto-generated method stub
        return (ResourceController<?, ?>) new TestControllerManager();
    }

    @Override
    public void unregisterResource(org.flexiblepower.rai.ControllableResource<?> resource) {
        logger.trace("unregisterResource({})", resource);
    }
}
