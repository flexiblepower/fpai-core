package org.flexiblepower.efi.timeshifter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.ControlSpaceUpdate;

/**
 * When an energy app allocates the energy flexibility of a time shifter it sends a time shifter allocation to the
 * appliance driver.
 * 
 * @author TNO
 * 
 */
public final class TimeShifterAllocation extends Allocation {

    private static final long serialVersionUID = -1435692490364313263L;

    public static final class SequentialProfileAllocation {

        /**
         * The unique id of the sequential profile that is allocated.
         */
        private final int sequentialProfileId;

        /**
         * The desired start time of allocation.
         */
        private final Date startTime;

        public SequentialProfileAllocation(int sequentialProfileId, Date startTime) {
            super();
            this.sequentialProfileId = sequentialProfileId;
            this.startTime = startTime;
        }

        public int getSequentialProfileId() {
            return sequentialProfileId;
        }

        public Date getStartTime() {
            return startTime;
        }

    }

    /**
     * Can be complete list or can be one at a time and everything in between
     */
    private final List<SequentialProfileAllocation> sequentialProfileAllocation;

    public TimeShifterAllocation(String resourceId,
                                 ControlSpaceUpdate resourceUpdate,
                                 Date timestamp,
                                 boolean isEmergencyAllocation,
                                 List<SequentialProfileAllocation> sequentialProfileAllocation) {
        super(resourceId, resourceUpdate, timestamp, isEmergencyAllocation);
        this.sequentialProfileAllocation = Collections.unmodifiableList(new ArrayList<SequentialProfileAllocation>(sequentialProfileAllocation));
    }

    public TimeShifterAllocation(String resourceId,
                                 ControlSpaceUpdate resourceUpdate,
                                 Date timestamp,
                                 boolean isEmergencyAllocation,
                                 SequentialProfileAllocation... sequentialProfileAllocation) {
        this(resourceId, resourceUpdate, timestamp, isEmergencyAllocation, Arrays.asList(sequentialProfileAllocation));
    }

    public List<SequentialProfileAllocation> getSequentialProfileAllocation() {
        return sequentialProfileAllocation;

    }
}
