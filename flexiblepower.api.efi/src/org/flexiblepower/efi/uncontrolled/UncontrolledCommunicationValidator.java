package org.flexiblepower.efi.uncontrolled;

import org.flexiblepower.efi.EfiResourceTypes;
import org.flexiblepower.rai.CommunicationValidator;
import org.flexiblepower.rai.ResourceType;

public class UncontrolledCommunicationValidator
		implements
		CommunicationValidator<UncontrolledAllocation, UncontrolledRegistration, UncontrolledUpdate> {

	@Override
	public ResourceType<UncontrolledAllocation, UncontrolledRegistration, UncontrolledUpdate> getResourceType() {
		return EfiResourceTypes.UNCONTROLLED;
	}

	@Override
	public void validateResourceHandshake(
			UncontrolledRegistration resourceHandshake)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateResourceUpdate(UncontrolledUpdate resourceUpdate)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateAllocation(UncontrolledAllocation allocation)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

}
