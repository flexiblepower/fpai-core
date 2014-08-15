package org.flexiblepower.efi;

import org.flexiblepower.efi.buffer.BufferAllocation;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferUpdate;
import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.rai.comm.AllocationRevoke;
import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRevoke;
import org.flexiblepower.ral.ResourceManager;

@Port(name = "controller",
      accepts = { BufferAllocation.class, AllocationRevoke.class },
      sends = { BufferRegistration.class, BufferUpdate.class, AllocationStatusUpdate.class, ControlSpaceRevoke.class },
      cardinality = Cardinality.SINGLE)
public interface BufferResourceManager extends ResourceManager {
}
