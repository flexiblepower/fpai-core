package org.flexiblepower.runtime.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.efi.EfiResourceTypes;
import org.flexiblepower.rai.ControllableResource;
import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestControllerManager extends IdentifyableObject implements ControllerManager {
    private static final Logger logger = LoggerFactory.getLogger(TestControllerManager.class);

    private final Map<ControllableResource, ResourceController<?, ?>> resourceControllers = new HashMap<ControllableResource, ResourceController<?, ?>>();

    @Override
    public ResourceController<?, ?> registerResource(ControllableResource<?> resource,
                                                     ResourceType<?, ?, ?> resourceType) {
        logger.trace("registerResource({})", resource);
        Assert.assertEquals(EfiResourceTypes.BUFFER, resourceType);
        ResourceController<?, ?> newRC = new TestBufferResourceController();
        resourceControllers.put(resource, newRC);
        ResourceWiringTest.resourceControllers.add(newRC);
        return newRC;

    }

    @Override
    public void unregisterResource(ControllableResource<?> resource) {
        logger.trace("unregisterResource({})", resource);
        Assert.assertTrue(resourceControllers.containsKey(resource));
        ResourceController rc = resourceControllers.get(resource);
        resourceControllers.remove(resource);
        ResourceWiringTest.resourceControllers.remove(rc);
        resourceControllers.remove(resource);
    }

    public void assertNrOfConnections(int i) {
        Assert.assertEquals(toString(), i, resourceControllers.size());
    }
}
