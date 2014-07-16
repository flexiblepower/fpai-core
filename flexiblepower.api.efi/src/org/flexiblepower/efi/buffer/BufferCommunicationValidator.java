package org.flexiblepower.efi.buffer;

import org.flexiblepower.efi.EfiResourceTypes;
import org.flexiblepower.rai.CommunicationValidator;
import org.flexiblepower.rai.ResourceType;

public class BufferCommunicationValidator
		implements
		CommunicationValidator<BufferAllocation, BufferRegistration, BufferUpdate> {

	@Override
	public ResourceType<BufferAllocation, BufferRegistration, BufferUpdate> getResourceType() {
		return EfiResourceTypes.BUFFER;
	}

	@Override
	public void validateControlSpaceRegistration(BufferRegistration resourceHandshake)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateControlSpaceUpdate(BufferUpdate resourceUpdate)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateAllocation(BufferAllocation allocation)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

}
