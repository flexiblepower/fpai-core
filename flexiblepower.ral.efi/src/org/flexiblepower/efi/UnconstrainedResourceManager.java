package org.flexiblepower.efi;

import org.flexiblepower.efi.unconstrained.UnconstrainedAllocation;
import org.flexiblepower.efi.unconstrained.UnconstrainedRegistration;
import org.flexiblepower.efi.unconstrained.UnconstrainedUpdate;
import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.messages.AllocationRevoke;
import org.flexiblepower.ral.messages.AllocationStatusUpdate;
import org.flexiblepower.ral.messages.ControlSpaceRevoke;

@Port(name = "controller",
      accepts = { UnconstrainedAllocation.class, AllocationRevoke.class },
      sends = { UnconstrainedRegistration.class,
               UnconstrainedUpdate.class,
               AllocationStatusUpdate.class,
               ControlSpaceRevoke.class },
      cardinality = Cardinality.SINGLE)
public interface UnconstrainedResourceManager extends ResourceManager {
}
