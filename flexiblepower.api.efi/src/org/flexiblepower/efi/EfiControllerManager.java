package org.flexiblepower.efi;

import org.flexiblepower.efi.buffer.BufferAllocation;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferUpdate;
import org.flexiblepower.efi.timeshifter.TimeShifterAllocation;
import org.flexiblepower.efi.timeshifter.TimeShifterRegistration;
import org.flexiblepower.efi.timeshifter.TimeShifterUpdate;
import org.flexiblepower.efi.unconstrained.UnconstrainedAllocation;
import org.flexiblepower.efi.unconstrained.UnconstrainedRegistration;
import org.flexiblepower.efi.unconstrained.UnconstrainedUpdate;
import org.flexiblepower.efi.uncontrolled.UncontrolledAllocation;
import org.flexiblepower.efi.uncontrolled.UncontrolledRegistration;
import org.flexiblepower.efi.uncontrolled.UncontrolledUpdate;
import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.Ports;
import org.flexiblepower.rai.comm.AllocationRevoke;
import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRevoke;

@Ports({ @Port(name = "buffer",
               accepts = { BufferRegistration.class,
                          BufferUpdate.class,
                          AllocationStatusUpdate.class,
                          ControlSpaceRevoke.class },
               sends = { BufferAllocation.class, AllocationRevoke.class },
               cardinality = Cardinality.MULTIPLE),
        @Port(name = "timeshifter",
              accepts = { TimeShifterRegistration.class,
                         TimeShifterUpdate.class,
                         AllocationStatusUpdate.class,
                         ControlSpaceRevoke.class },
              sends = { TimeShifterAllocation.class, AllocationRevoke.class },
              cardinality = Cardinality.MULTIPLE),
        @Port(name = "unconstrained",
              accepts = { UnconstrainedRegistration.class,
                         UnconstrainedUpdate.class,
                         AllocationStatusUpdate.class,
                         ControlSpaceRevoke.class },
              sends = { UnconstrainedAllocation.class, AllocationRevoke.class },
              cardinality = Cardinality.MULTIPLE),
        @Port(name = "uncontrolled",
              accepts = { UncontrolledRegistration.class,
                         UncontrolledUpdate.class,
                         AllocationStatusUpdate.class,
                         ControlSpaceRevoke.class },
              sends = { UncontrolledAllocation.class, AllocationRevoke.class },
              cardinality = Cardinality.MULTIPLE) })
public interface EfiControllerManager extends Endpoint {

}
