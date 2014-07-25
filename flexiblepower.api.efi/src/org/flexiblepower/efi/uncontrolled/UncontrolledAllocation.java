package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public class UncontrolledAllocation extends Allocation {

    private static final long serialVersionUID = -6113496967677840815L;

    private final Date startTime;
    private final Element[] profile;

    public UncontrolledAllocation(String resourceId,
                                  ControlSpaceUpdate resourceUpdate,
                                  Date timestamp,
                                  boolean isEmergencyAllocation,
                                  Date startTime) {
        super(resourceId, resourceUpdate, timestamp, isEmergencyAllocation);

        this.startTime = startTime;
        profile = null;
        // TODO
    }

    public static class Element {
        private final Measurable<Duration> duration = null;
        private final double maxConsumption;
        private final double maxProduction;

        public Element(double maxConsumption, double maxProduction) {
            super();
            this.maxConsumption = maxConsumption;
            this.maxProduction = maxProduction;
        }

    }
}
