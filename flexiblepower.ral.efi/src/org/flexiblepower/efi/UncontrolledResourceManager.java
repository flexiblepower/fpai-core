package org.flexiblepower.efi;

import org.flexiblepower.efi.uncontrolled.UncontrolledAllocation;
import org.flexiblepower.efi.uncontrolled.UncontrolledRegistration;
import org.flexiblepower.efi.uncontrolled.UncontrolledUpdate;
import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.messages.AllocationRevoke;
import org.flexiblepower.ral.messages.AllocationStatusUpdate;
import org.flexiblepower.ral.messages.ControlSpaceRevoke;

@Port(name = "controller",
      accepts = { UncontrolledAllocation.class, AllocationRevoke.class },
      sends = { UncontrolledRegistration.class,
               UncontrolledUpdate.class,
               AllocationStatusUpdate.class,
               ControlSpaceRevoke.class },
      cardinality = Cardinality.SINGLE)
public interface UncontrolledResourceManager extends ResourceManager {
}
