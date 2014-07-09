package org.flexiblepower.efi.timeshifter;

import java.util.Date;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public class TimeShifterUpdate extends ControlSpaceUpdate {

	private final Date endBefore;
	private final List<SequentialProfile> timeshiferProfiles;

	public TimeShifterUpdate(String resourceId, Date timestamp,
			Date validFrom, Measurable<Duration> allocationDelay,
			Date endBefore, List<SequentialProfile> timeshiferProfiles) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.endBefore = endBefore;
		this.timeshiferProfiles = timeshiferProfiles;
	}

}
