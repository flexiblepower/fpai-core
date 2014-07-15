package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public abstract class ResourceType<A extends Allocation, CSR extends ControlSpaceRegistration, CSU extends ControlSpaceUpdate> {

    public abstract Class<? extends CommunicationValidator<A, CSR, CSU>> getCommunicationValidatorClass();

    public abstract Class<A> getAllocationClass();

    public abstract Class<CSR> getControlSpaceRegistrationClass();

    public abstract Class<CSU> getControlSpaceUpdateClass();

}
