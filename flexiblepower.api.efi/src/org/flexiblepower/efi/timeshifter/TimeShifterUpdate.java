package org.flexiblepower.efi.timeshifter;

import java.util.Date;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public class TimeShifterUpdate extends ControlSpaceUpdate {

	private final Date endBefore;
	private final List<SequentialProfile> timeshifterProfiles;

	public TimeShifterUpdate(String resourceId, Date timestamp,
			Date validFrom, Measurable<Duration> allocationDelay,
			Date endBefore, List<SequentialProfile> timeshifterProfiles) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.endBefore = endBefore;
		this.timeshifterProfiles = timeshifterProfiles;
	}
	
	protected List<SequentialProfile> getTimeShifterProfiles(){
		return timeshifterProfiles;
		
	}

}
