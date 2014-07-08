package org.flexiblepower.efi.buffer;

import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceType;

public interface BufferResourceController extends
		ResourceController<BufferCapabilities, BufferUpdate> {

	public static final ResourceType<BufferAllocation, BufferCapabilities, BufferUpdate> BUFFER = new ResourceType<BufferAllocation, BufferCapabilities, BufferUpdate>() {

	};

}
