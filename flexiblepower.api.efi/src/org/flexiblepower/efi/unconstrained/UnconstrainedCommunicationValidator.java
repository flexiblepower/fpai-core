package org.flexiblepower.efi.unconstrained;

import org.flexiblepower.efi.EfiResourceTypes;
import org.flexiblepower.rai.CommunicationValidator;
import org.flexiblepower.rai.ResourceType;

public class UnconstrainedCommunicationValidator implements
                                                CommunicationValidator<UnconstrainedAllocation, UnconstrainedRegistration, UnconstrainedUpdate> {

    @Override
    public ResourceType<UnconstrainedAllocation, UnconstrainedRegistration, UnconstrainedUpdate> getResourceType() {
        return EfiResourceTypes.UNCONSTRAINED;
    }

    @Override
    public void
            validateControlSpaceRegistration(UnconstrainedRegistration resourceHandshake) throws IllegalArgumentException,
                                                                                         IllegalStateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void validateControlSpaceUpdate(UnconstrainedUpdate resourceUpdate) throws IllegalArgumentException,
                                                                              IllegalStateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void validateAllocation(UnconstrainedAllocation allocation) throws IllegalArgumentException,
                                                                      IllegalStateException {
        // TODO Auto-generated method stub

    }

}
