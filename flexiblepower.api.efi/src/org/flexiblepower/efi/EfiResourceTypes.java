package org.flexiblepower.efi;

import org.flexiblepower.efi.buffer.BufferAllocation;
import org.flexiblepower.efi.buffer.BufferCommunicationValidator;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferStateUpdate;
import org.flexiblepower.efi.buffer.BufferUpdate;
import org.flexiblepower.efi.timeshifter.TimeShifterAllocation;
import org.flexiblepower.efi.timeshifter.TimeShifterCommunicationValidator;
import org.flexiblepower.efi.timeshifter.TimeShifterRegistration;
import org.flexiblepower.efi.timeshifter.TimeShifterUpdate;
import org.flexiblepower.efi.unconstrained.UnconstrainedAllocation;
import org.flexiblepower.efi.unconstrained.UnconstrainedCommunicationValidator;
import org.flexiblepower.efi.unconstrained.UnconstrainedRegistration;
import org.flexiblepower.efi.unconstrained.UnconstrainedUpdate;
import org.flexiblepower.efi.uncontrolled.UncontrolledAllocation;
import org.flexiblepower.efi.uncontrolled.UncontrolledCommunicationValidator;
import org.flexiblepower.efi.uncontrolled.UncontrolledRegistration;
import org.flexiblepower.efi.uncontrolled.UncontrolledUpdate;
import org.flexiblepower.rai.CommunicationValidator;
import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceType;

public interface EfiResourceTypes extends
		ResourceController<BufferRegistration, BufferStateUpdate> {

	public static final ResourceType<BufferAllocation, BufferRegistration, BufferUpdate> BUFFER = new ResourceType<BufferAllocation, BufferRegistration, BufferUpdate>() {

		@Override
		public Class<BufferAllocation> getAllocationClass() {
			return BufferAllocation.class;
		}

		@Override
		public Class<BufferRegistration> getControlSpaceRegistrationClass() {
			return BufferRegistration.class;
		}

		@Override
		public Class<BufferUpdate> getControlSpaceUpdateClass() {
			return BufferUpdate.class;
		}

		@Override
		public Class<? extends CommunicationValidator<BufferAllocation, BufferRegistration, BufferUpdate>> getCommunicationValidatorClass() {
			return BufferCommunicationValidator.class;
		}

		@Override
		public String getName() {
			return "Buffer";
		}

	};

	public static final ResourceType<TimeShifterAllocation, TimeShifterRegistration, TimeShifterUpdate> TIMESHIFTER = new ResourceType<TimeShifterAllocation, TimeShifterRegistration, TimeShifterUpdate>() {

		@Override
		public Class<TimeShifterAllocation> getAllocationClass() {
			return TimeShifterAllocation.class;
		}

		@Override
		public Class<TimeShifterRegistration> getControlSpaceRegistrationClass() {
			return TimeShifterRegistration.class;
		}

		@Override
		public Class<TimeShifterUpdate> getControlSpaceUpdateClass() {
			return TimeShifterUpdate.class;
		}

		@Override
		public Class<? extends CommunicationValidator<TimeShifterAllocation, TimeShifterRegistration, TimeShifterUpdate>> getCommunicationValidatorClass() {
			return TimeShifterCommunicationValidator.class;
		}

		@Override
		public String getName() {
			return "TimeShifter";
		}
	};

	public static final ResourceType<UnconstrainedAllocation, UnconstrainedRegistration, UnconstrainedUpdate> UNCONSTRAINED = new ResourceType<UnconstrainedAllocation, UnconstrainedRegistration, UnconstrainedUpdate>() {

		@Override
		public Class<UnconstrainedAllocation> getAllocationClass() {
			return UnconstrainedAllocation.class;
		}

		@Override
		public Class<UnconstrainedRegistration> getControlSpaceRegistrationClass() {
			return UnconstrainedRegistration.class;
		}

		@Override
		public Class<UnconstrainedUpdate> getControlSpaceUpdateClass() {
			return UnconstrainedUpdate.class;
		}

		@Override
		public Class<? extends CommunicationValidator<UnconstrainedAllocation, UnconstrainedRegistration, UnconstrainedUpdate>> getCommunicationValidatorClass() {
			return UnconstrainedCommunicationValidator.class;
		}

		@Override
		public String getName() {
			return "Unconstrained";
		}
	};

	public static final ResourceType<UncontrolledAllocation, UncontrolledRegistration, UncontrolledUpdate> UNCONTROLLED = new ResourceType<UncontrolledAllocation, UncontrolledRegistration, UncontrolledUpdate>() {

		@Override
		public Class<UncontrolledAllocation> getAllocationClass() {
			return UncontrolledAllocation.class;
		}

		@Override
		public Class<UncontrolledRegistration> getControlSpaceRegistrationClass() {
			return UncontrolledRegistration.class;
		}

		@Override
		public Class<UncontrolledUpdate> getControlSpaceUpdateClass() {
			return UncontrolledUpdate.class;
		}

		@Override
		public Class<? extends CommunicationValidator<UncontrolledAllocation, UncontrolledRegistration, UncontrolledUpdate>> getCommunicationValidatorClass() {
			return UncontrolledCommunicationValidator.class;
		}

		@Override
		public String getName() {
			return "Uncontrolled";
		}
	};

}
