package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public interface ResourceType<A extends Allocation, CSR extends ControlSpaceRegistration, CSU extends ControlSpaceUpdate> {

    String getName();

    Class<? extends CommunicationValidator<A, CSR, CSU>> getCommunicationValidatorClass();

    Class<A> getAllocationClass();

    Class<CSR> getControlSpaceRegistrationClass();

    Class<CSU> getControlSpaceUpdateClass();

}
