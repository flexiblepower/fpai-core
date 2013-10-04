package org.flexiblepower.rai;

public interface Controller<CS extends ControlSpace> {
    void controlSpaceUpdated(ControllableResource<CS> resource, CS controlSpace);
}
