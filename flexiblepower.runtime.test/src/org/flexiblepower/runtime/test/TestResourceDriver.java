package org.flexiblepower.runtime.test;

import junit.framework.Assert;

import org.flexiblepower.observation.Observation;
import org.flexiblepower.observation.ObservationConsumer;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceState;

public class TestResourceDriver extends IdentifyableObject implements
                                                          ResourceDriver<ResourceState, ResourceControlParameters> {
    private ObservationConsumer<? super ResourceState> consumer;

    public void assertCorrectWiring(ObservationConsumer<? super ResourceState> expectedConsumer) {
        Assert.assertEquals(toString(), expectedConsumer, consumer);
    }

    @Override
    public void subscribe(ObservationConsumer<? super ResourceState> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void unsubscribe(ObservationConsumer<? super ResourceState> consumer) {
        Assert.assertEquals(this.consumer, consumer);
        this.consumer = null;
    }

    @Override
    public void setControlParameters(ResourceControlParameters resourceControlParameters) {
    }

    @Override
    public Observation<? extends ResourceState> getLastObservation() {
        // not used in test
        return null;
    }
}
