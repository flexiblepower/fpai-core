package org.flexiblepower.runtime.test;

import junit.framework.Assert;

import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferUpdate;
import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceMessageSubmitter;
import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRevoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBufferResourceController extends IdentifyableObject implements
                                                                    ResourceController<BufferRegistration, BufferUpdate> {
    private static final Logger logger = LoggerFactory.getLogger(TestBufferResourceController.class);
    private ResourceMessageSubmitter resourceMessageSubmitter;
    private BufferRegistration lastBufferRegistration;

    @Override
    public void initialize(BufferRegistration controlSpaceRegistration,
                           ResourceMessageSubmitter resourceMessageSubmitter) {
        logger.trace("initialize({},{})", controlSpaceRegistration, resourceMessageSubmitter);
        lastBufferRegistration = controlSpaceRegistration;
        this.resourceMessageSubmitter = resourceMessageSubmitter;
    }

    public void assertReceivedBufferRegistration(BufferRegistration bufferRegistration) {
        Assert.assertEquals(bufferRegistration, lastBufferRegistration);
    }

    @Override
    public void handleResourceUpdate(BufferUpdate resourceUpdate) {
        logger.trace("handleResourceUpdate({})", resourceUpdate);
    }

    @Override
    public void handleAllocationStatusUpdate(AllocationStatusUpdate allocationStatusUpdate) {
        logger.trace("handleAllocationStatusUpdate({})", allocationStatusUpdate);
    }

    @Override
    public void handleControlSpaceRevoke(ControlSpaceRevoke controlSpaceRevoke) {
        logger.trace("handleControlSpaceRevoke({})", controlSpaceRevoke);
    }

}
