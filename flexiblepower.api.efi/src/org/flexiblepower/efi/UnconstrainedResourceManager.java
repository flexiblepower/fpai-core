package org.flexiblepower.efi;

import org.flexiblepower.efi.unconstrained.UnconstrainedAllocation;
import org.flexiblepower.efi.unconstrained.UnconstrainedRegistration;
import org.flexiblepower.efi.unconstrained.UnconstrainedUpdate;
import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.rai.comm.AllocationRevoke;
import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRevoke;
import org.flexiblepower.ral.ResourceManager;

@Port(name = "controller",
      accepts = { UnconstrainedAllocation.class, AllocationRevoke.class },
      sends = { UnconstrainedRegistration.class,
               UnconstrainedUpdate.class,
               AllocationStatusUpdate.class,
               ControlSpaceRevoke.class },
      cardinality = Cardinality.SINGLE)
public interface UnconstrainedResourceManager extends ResourceManager {
}
