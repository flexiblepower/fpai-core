package org.flexiblepower.runtime.test;

import junit.framework.Assert;

import org.flexiblepower.observation.Observation;
import org.flexiblepower.observation.ObservationProvider;
import org.flexiblepower.rai.old.Allocation;
import org.flexiblepower.rai.old.ControlSpace;
import org.flexiblepower.rai.old.Controller;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;

public class TestResourceManager extends IdentifyableObject implements
                                                           ResourceManager<ControlSpace, ResourceState, ResourceControlParameters> {
    private Controller<? super ControlSpace> controller;
    private ResourceDriver<? extends ResourceState, ? super ResourceControlParameters> driver;

    public void assertCorrectWiring(Controller<ControlSpace> expectedController,
                                    ResourceDriver<ResourceState, ResourceControlParameters> expectedDriver) {
        Assert.assertEquals(toString(), expectedController, controller);
        Assert.assertEquals(toString(), expectedDriver, driver);
    }

    @Override
    public void consume(ObservationProvider<? extends ResourceState> source,
                        Observation<? extends ResourceState> observation) {
    }

    @Override
    public void handleAllocation(Allocation allocation) {
    }

    @Override
    public Class<ControlSpace> getControlSpaceType() {
        return ControlSpace.class;
    }

    @Override
    public void setController(Controller<? super ControlSpace> controller) {
        this.controller = controller;
    }

    @Override
    public void unsetController(Controller<? super ControlSpace> controller) {
        this.controller = null;

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
}
