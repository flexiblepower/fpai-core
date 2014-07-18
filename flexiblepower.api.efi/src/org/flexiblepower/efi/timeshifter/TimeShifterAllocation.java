package org.flexiblepower.efi.timeshifter;

import java.util.Date;
import java.util.List;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public class TimeShifterAllocation extends Allocation {

	private static final long serialVersionUID = -1435692490364313263L;

	public class SequentialProfileAllocation {
		private final int sequentialProfileId;
		private final Date startTime;

		public SequentialProfileAllocation(int sequentialProfileId,
				Date startTime) {
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

	// Can be complete list or can be one at a time and everything in between
	private List<SequentialProfileAllocation> sequentialProfileAllocation;

	public TimeShifterAllocation(String resourceId,
			ControlSpaceUpdate resourceUpdate, Date timestamp,
			List<SequentialProfileAllocation> sequentialProfileAllocation) {
		super(resourceId, resourceUpdate, timestamp);
		this.sequentialProfileAllocation = sequentialProfileAllocation;
	}
	
	protected List<SequentialProfileAllocation> getSequentialProfileAllocation(){
		return sequentialProfileAllocation;
		
	}


}
