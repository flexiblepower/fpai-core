package org.flexiblepower.runtime.test;

import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceMessageSubmitter;
import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResourceController<CSR extends ControlSpaceRegistration, CSU extends ControlSpaceUpdate> extends
                                                                                                          IdentifyableObject implements
                                                                                                                            ResourceController<CSR, CSU> {
    private static final Logger logger = LoggerFactory.getLogger(TestResourceController.class);

    @Override
    public void initialize(CSR controlSpaceRegistration, ResourceMessageSubmitter resourceMessageSubmitter) {
        logger.trace("initialize({},{})", controlSpaceRegistration, resourceMessageSubmitter);
    }

    @Override
    public void handleResourceUpdate(CSU resourceUpdate) {
        logger.trace("handleResourceUpdate({})", resourceUpdate);
    }

    @Override
    public void handleAllocationStatusUpdate(AllocationStatusUpdate allocationStatusUpdate) {
        logger.trace("handleAllocationStatusUpdate({})", allocationStatusUpdate);
    }

}
