package org.flexiblepower.efi;

import org.flexiblepower.efi.timeshifter.TimeShifterAllocation;
import org.flexiblepower.efi.timeshifter.TimeShifterRegistration;
import org.flexiblepower.efi.timeshifter.TimeShifterUpdate;
import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.rai.comm.AllocationRevoke;
import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRevoke;
import org.flexiblepower.ral.ResourceManager;

@Port(name = "controller",
      accepts = { TimeShifterAllocation.class, AllocationRevoke.class },
      sends = { TimeShifterRegistration.class,
               TimeShifterUpdate.class,
               AllocationStatusUpdate.class,
               ControlSpaceRevoke.class },
      cardinality = Cardinality.SINGLE)
public interface TimeShifterResourceManager extends ResourceManager {
}
