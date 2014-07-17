package org.flexiblepower.runtime.test;

import junit.framework.Assert;

import org.flexiblepower.observation.Observation;
import org.flexiblepower.observation.ObservationProvider;
import org.flexiblepower.rai.ResourceMessageSubmitter;
import org.flexiblepower.rai.ResourceType;
import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResourceManager<A extends Allocation> extends IdentifyableObject implements
                                                                                 ResourceManager<A, ResourceState, ResourceControlParameters> {

    private static final Logger logger = LoggerFactory.getLogger(TestResourceManager.class);

    private ResourceDriver<? extends ResourceState, ? super ResourceControlParameters> driver;

    public void assertCorrectResourceDriver(ResourceDriver<ResourceState, ResourceControlParameters> expectedDriver) {
        Assert.assertEquals(toString(), expectedDriver, driver);
    }

    @Override
    public void consume(ObservationProvider<? extends ResourceState> source,
                        Observation<? extends ResourceState> observation) {
    }

    @Override
    public void registerDriver(ResourceDriver<? extends ResourceState, ? super ResourceControlParameters> driver) {
        this.driver = driver;
        driver.subscribe(this);
    }

    @Override
    public void unregisterDriver(ResourceDriver<? extends ResourceState, ? super ResourceControlParameters> driver) {
        this.driver = null;
        driver.unsubscribe(this);
    }

    @Override
    public ResourceType<A, ?, ?> getResourceType() {
        return null;
    }

    @Override
    public void initialize(ResourceMessageSubmitter resourceMessageSubmitter) {
        logger.trace("initialize({})", resourceMessageSubmitter);
    }

    @Override
    public void handleAllocation(A allocation) {
        logger.trace("handleAllocation({})", allocation);

    }

    @Override
    public void disconnect() {
        logger.trace("disconnect()");
    }
}
