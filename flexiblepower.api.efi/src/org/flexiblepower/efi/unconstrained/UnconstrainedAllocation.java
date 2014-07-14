package org.flexiblepower.efi.unconstrained;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public class UnconstrainedAllocation extends Allocation {

	public static class RunningModeSelector {
		private int runningModeId;
		private Date startTime;
	}

	public UnconstrainedAllocation(String resourceId, ControlSpaceUpdate resourceUpdate,
			Date timestamp, Set<RunningModeSelector> runningModeSelectors) {
		super(resourceId, resourceUpdate, timestamp);
		this.runningModeSelectors = runningModeSelectors;
	}

	private final Set<RunningModeSelector> runningModeSelectors;
}
