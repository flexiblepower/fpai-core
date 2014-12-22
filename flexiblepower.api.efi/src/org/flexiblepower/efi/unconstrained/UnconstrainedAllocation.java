package org.flexiblepower.efi.unconstrained;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.ControlSpaceUpdate;
import org.flexiblepower.time.TimeService;

/**
 * This class is derived from {@link Allocation} and contains specific allocation information for a unconstrained
 * appliance.
 */
public class UnconstrainedAllocation extends Allocation {
    private final Set<RunningModeSelector> runningModeSelectors;

    /**
     * Constructs a new {@link UnconstrainedAllocation} object as a response to a specific {@link ControlSpaceUpdate}.
     *
     * @param resourceUpdate
     *            The {@link ControlSpaceUpdate} object to which this {@link Allocation} is responding to.
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param isEmergencyAllocation
     *            This Boolean value is optional and is true when a grid emergency situation occurs. (e.g. congestion,
     *            black start etc.) The energy app then strongly advices the appliance driver to adapt to the sent
     *            allocation in order to maintain grid stability.
     * @param runningModeSelectors
     */
    public UnconstrainedAllocation(UnconstrainedUpdate resourceUpdate,
                                   Date timestamp,
                                   boolean isEmergencyAllocation,
                                   Collection<RunningModeSelector> runningModeSelectors) {
        super(timestamp, resourceUpdate, isEmergencyAllocation);
        if (runningModeSelectors == null) {
            throw new NullPointerException("runningModeSelectors");
        }
        this.runningModeSelectors = Collections.unmodifiableSet(new HashSet<RunningModeSelector>(runningModeSelectors));
    }

    /**
     * @return A set of zero or more {@link RunningModeSelector} objects.
     */
    public Set<RunningModeSelector> getRunningModeSelectors() {
        return runningModeSelectors;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + runningModeSelectors.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UnconstrainedAllocation other = (UnconstrainedAllocation) obj;
        return runningModeSelectors.equals(other.runningModeSelectors);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("runningModeSelectors=").append(runningModeSelectors).append(", ");
    }
}
