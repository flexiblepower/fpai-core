package org.flexiblepower.efi;

import org.flexiblepower.efi.buffer.BufferAllocation;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferStateUpdate;
import org.flexiblepower.efi.timeshifter.TimeShifterAllocation;
import org.flexiblepower.efi.timeshifter.TimeShifterRegistration;
import org.flexiblepower.efi.timeshifter.TimeShifterUpdate;
import org.flexiblepower.efi.unconstrained.UnconstrainedRegistration;
import org.flexiblepower.efi.unconstrained.UnconstrainedUpdate;
import org.flexiblepower.efi.uncontrolled.UncontrolledAllocation;
import org.flexiblepower.efi.uncontrolled.UncontrolledRegistration;
import org.flexiblepower.efi.uncontrolled.UncontrolledUpdate;
import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceType;

public interface EfiResourceTypes extends
		ResourceController<BufferRegistration, BufferStateUpdate> {

	public static final ResourceType<BufferAllocation, BufferRegistration, BufferStateUpdate> BUFFER = new ResourceType<BufferAllocation, BufferRegistration, BufferStateUpdate>() {

		@Override
		public Class<BufferAllocation> getAllocationClass() {
			return BufferAllocation.class;
		}

		@Override
		public Class<BufferRegistration> getControlSpaceRegistrationClass() {
			return BufferRegistration.class;
		}

		@Override
		public Class<BufferStateUpdate> getControlSpaceUpdateClass() {
			return BufferStateUpdate.class;
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
	};

	public static final ResourceType<UncontrolledAllocation, UnconstrainedRegistration, UnconstrainedUpdate> UNCONSTRAINED = new ResourceType<UncontrolledAllocation, UnconstrainedRegistration, UnconstrainedUpdate>() {

		@Override
		public Class<UncontrolledAllocation> getAllocationClass() {
			return UncontrolledAllocation.class;
		}

		@Override
		public Class<UnconstrainedRegistration> getControlSpaceRegistrationClass() {
			return UnconstrainedRegistration.class;
		}

		@Override
		public Class<UnconstrainedUpdate> getControlSpaceUpdateClass() {
			return UnconstrainedUpdate.class;
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
	};

}
