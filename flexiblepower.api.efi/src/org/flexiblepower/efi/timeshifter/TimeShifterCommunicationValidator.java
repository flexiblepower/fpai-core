package org.flexiblepower.efi.timeshifter;

import org.flexiblepower.efi.EfiResourceTypes;
import org.flexiblepower.rai.CommunicationValidator;
import org.flexiblepower.rai.ResourceType;

public class TimeShifterCommunicationValidator
		implements
		CommunicationValidator<TimeShifterAllocation, TimeShifterRegistration, TimeShifterUpdate> {

	@Override
	public ResourceType<TimeShifterAllocation, TimeShifterRegistration, TimeShifterUpdate> getResourceType() {
		return EfiResourceTypes.TIMESHIFTER;
	}

	@Override
	public void validateControlSpaceRegistration(
			TimeShifterRegistration resourceHandshake)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateControlSpaceUpdate(TimeShifterUpdate resourceUpdate)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateAllocation(TimeShifterAllocation allocation)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub

	}

}
