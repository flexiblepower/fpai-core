package org.flexiblepower.efi.buffer;

import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceType;

public interface BufferResourceController extends
		ResourceController<BufferRegistration, BufferStateUpdate> {

	public static final ResourceType<BufferAllocation, BufferRegistration, BufferStateUpdate> BUFFER = new ResourceType<BufferAllocation, BufferRegistration, BufferStateUpdate>() {

	};

}
