package org.flexiblepower.rai;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public interface CommunicationValidator<A extends Allocation, CSR extends ControlSpaceRegistration, CSU extends ControlSpaceUpdate> {

    public ResourceType<A, CSR, CSU> getResourceType();

    public void validateControlSpaceRegistration(CSR controlSpaceRegistration) throws IllegalArgumentException,
                                                                              IllegalStateException;

    public void validateControlSpaceUpdate(CSU controlSpaceUpdate) throws IllegalArgumentException,
                                                                  IllegalStateException;

    public void validateAllocation(A allocation) throws IllegalArgumentException, IllegalStateException;

}
