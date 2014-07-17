package org.flexiblepower.runtime.test;

import java.util.Date;
import java.util.HashSet;

import javax.measure.unit.SI;

import junit.framework.Assert;

import org.flexiblepower.efi.EfiResourceTypes;
import org.flexiblepower.efi.buffer.BufferAllocation;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferRegistration.ActuatorCapabilities;
import org.flexiblepower.observation.Observation;
import org.flexiblepower.observation.ObservationProvider;
import org.flexiblepower.rai.ResourceMessageSubmitter;
import org.flexiblepower.rai.ResourceType;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBufferResourceManager extends IdentifyableObject implements
                                                                 ResourceManager<BufferAllocation, ResourceState, ResourceControlParameters> {

    private static final Logger logger = LoggerFactory.getLogger(TestBufferResourceManager.class);

    private ResourceDriver<? extends ResourceState, ? super ResourceControlParameters> driver;
    private final String resourceId;

    private ResourceMessageSubmitter resourceMessageSubmitter;

    public TestBufferResourceManager(String resourceId) {
        this.resourceId = resourceId;
    }

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
    public ResourceType<BufferAllocation, ?, ?> getResourceType() {
        return EfiResourceTypes.BUFFER;
    }

    @Override
    public void initialize(ResourceMessageSubmitter resourceMessageSubmitter) {
        logger.trace("initialize({})", resourceMessageSubmitter);
        this.resourceMessageSubmitter = resourceMessageSubmitter;
    }

    public BufferRegistration sendBufferRegistration() {
        HashSet<Commodity> commodities = new HashSet<Commodity>();
        commodities.add(Commodity.ELECTRICITY);
        ActuatorCapabilities actuatorCapabilities = new BufferRegistration.ActuatorCapabilities(0,
                                                                                                "actuator1",
                                                                                                commodities);
        HashSet<ActuatorCapabilities> actuatorCapabilitiesSet = new HashSet<BufferRegistration.ActuatorCapabilities>();
        actuatorCapabilitiesSet.add(actuatorCapabilities);
        BufferRegistration br = new BufferRegistration(resourceId,
                                                       new Date(),
                                                       "temp",
                                                       SI.CELSIUS,
                                                       actuatorCapabilitiesSet);
        resourceMessageSubmitter.submitResourceMessage(br);
        return br;
    }

    @Override
    public void handleAllocation(BufferAllocation allocation) {
        logger.trace("handleAllocation({})", allocation);

    }

    @Override
    public void disconnect() {
        logger.trace("disconnect()");
    }
}
