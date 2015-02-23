package org.flexiblepower.efi;

import org.flexiblepower.efi.buffer.BufferAllocation;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferUpdate;
import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.messages.AllocationRevoke;
import org.flexiblepower.ral.messages.AllocationStatusUpdate;
import org.flexiblepower.ral.messages.ControlSpaceRevoke;

@Port(name = "controller",
      accepts = { BufferAllocation.class, AllocationRevoke.class },
      sends = { BufferRegistration.class, BufferUpdate.class, AllocationStatusUpdate.class, ControlSpaceRevoke.class },
      cardinality = Cardinality.SINGLE)
public interface BufferResourceManager extends ResourceManager {
}
