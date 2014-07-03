package org.flexiblepower.runtime.test;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.rai.comm.ControlSpace;
import org.flexiblepower.rai.comm.ControllableResource;
import org.flexiblepower.rai.comm.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestControllerManager extends IdentifyableObject implements ControllerManager, Controller<ControlSpace> {
    private static final Logger logger = LoggerFactory.getLogger(TestControllerManager.class);

    @Override
    public void registerResource(ControllableResource<?> resource) {
        logger.trace("registerResource({})", resource);
        resource.setController(this);
    }

    @Override
    public void unregisterResource(ControllableResource<?> resource) {
        logger.trace("unregisterResource({})", resource);
        resource.unsetController(this);
    }

    @Override
    public void controlSpaceUpdated(ControllableResource<? extends ControlSpace> resource, ControlSpace controlSpace) {
        logger.trace("controlSpaceUpdated({}, {})", resource, controlSpace);
    }
}
