package org.flexiblepower.rai;

public interface Controller<CS extends ControlSpace> {
    void controlSpaceUpdated(ControllableResource<? extends CS> resource, CS controlSpace);
}
