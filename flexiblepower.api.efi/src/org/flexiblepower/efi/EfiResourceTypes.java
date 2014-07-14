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
	};

	public static final ResourceType<TimeShifterAllocation, TimeShifterRegistration, TimeShifterUpdate> TIMESHIFTER = new ResourceType<TimeShifterAllocation, TimeShifterRegistration, TimeShifterUpdate>() {
	};

	public static final ResourceType<UncontrolledAllocation, UnconstrainedRegistration, UnconstrainedUpdate> UNCONSTRAINED = new ResourceType<UncontrolledAllocation, UnconstrainedRegistration, UnconstrainedUpdate>() {
	};

	public static final ResourceType<UncontrolledAllocation, UncontrolledRegistration, UncontrolledUpdate> UNCONTROLLED = new ResourceType<UncontrolledAllocation, UncontrolledRegistration, UncontrolledUpdate>() {
	};

}
