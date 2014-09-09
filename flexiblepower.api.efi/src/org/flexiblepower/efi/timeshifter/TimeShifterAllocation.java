package org.flexiblepower.efi.timeshifter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.ControlSpaceUpdate;
import org.flexiblepower.time.TimeService;

/**
 * When an energy app allocates the energy flexibility of a time shifter it sends a time shifter allocation to the
 * appliance driver.
 */
public final class TimeShifterAllocation extends Allocation {
    private static final long serialVersionUID = -1435692490364313263L;

    private final List<SequentialProfileAllocation> sequentialProfileAllocation;

    /**
     * Constructs a new {@link TimeShifterAllocation} object as a response to a specific {@link ControlSpaceUpdate}.
     *
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param resourceUpdate
     *            The {@link ControlSpaceUpdate} object to which this {@link Allocation} is responding to.
     * @param isEmergencyAllocation
     *            This Boolean value is optional and is true when a grid emergency situation occurs. (e.g. congestion,
     *            black start etc.) The energy app then strongly advices the appliance driver to adapt to the sent
     *            allocation in order to maintain grid stability.
     * @param sequentialProfileAllocation
     *            A list containing one or more SequentialProfileAllocations.
     */
    public TimeShifterAllocation(TimeShifterUpdate resourceUpdate,
                                 Date timestamp,
                                 boolean isEmergencyAllocation,
                                 List<SequentialProfileAllocation> sequentialProfileAllocation) {
        super(timestamp, resourceUpdate, isEmergencyAllocation);
        if (sequentialProfileAllocation == null) {
            throw new NullPointerException("sequentialProfileAllocation");
        }
        if (sequentialProfileAllocation.isEmpty()) {
            throw new IllegalArgumentException("sequentialProfileAllocation is empty");
        }
        this.sequentialProfileAllocation = Collections.unmodifiableList(new ArrayList<SequentialProfileAllocation>(sequentialProfileAllocation));
    }

    /**
     * Constructs a new {@link TimeShifterAllocation} object as a response to a specific {@link ControlSpaceUpdate}.
     *
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param resourceUpdate
     *            The {@link ControlSpaceUpdate} object to which this {@link Allocation} is responding to.
     * @param isEmergencyAllocation
     *            This Boolean value is optional and is true when a grid emergency situation occurs. (e.g. congestion,
     *            black start etc.) The energy app then strongly advices the appliance driver to adapt to the sent
     *            allocation in order to maintain grid stability.
     * @param sequentialProfileAllocation
     *            A list containing one or more SequentialProfileAllocations.
     */
    public TimeShifterAllocation(TimeShifterUpdate resourceUpdate,
                                 Date timestamp,
                                 boolean isEmergencyAllocation,
                                 SequentialProfileAllocation... sequentialProfileAllocation) {
        this(resourceUpdate, timestamp, isEmergencyAllocation, Arrays.asList(sequentialProfileAllocation));
    }

    /**
     * @return A list containing one or more SequentialProfileAllocations.
     */
    public List<SequentialProfileAllocation> getSequentialProfileAllocation() {
        return sequentialProfileAllocation;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + sequentialProfileAllocation.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        TimeShifterAllocation other = (TimeShifterAllocation) obj;
        return other.sequentialProfileAllocation.equals(sequentialProfileAllocation);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("sequentialProfileAllocation=").append(sequentialProfileAllocation).append(", ");
    }
}
