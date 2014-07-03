package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ResourceUpdate;

public class BufferAllocation extends Allocation {

	public static class RunningModeSelector {
		private int actuatorId;
		private int runningModeId;
		private Date startTime;
	}

	public BufferAllocation(String resourceId, ResourceUpdate resourceUpdate,
			Date timestamp, Set<RunningModeSelector> runningModeSelectors) {
		super(resourceId, resourceUpdate, timestamp);
		this.runningModeSelectors = runningModeSelectors;
	}

	private final Set<RunningModeSelector> runningModeSelectors;
}
