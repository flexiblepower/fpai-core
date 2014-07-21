package org.flexiblepower.efi.unconstrained;

import java.util.Date;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.efi.unconstrained.RunningMode;
import org.flexiblepower.efi.util.Timer;

public class UnconstrainedSystemDescription extends UnconstrainedUpdate{
	
	// Timers associated with this Unconstrained appliance
	private final List<Timer> timerList;

	// List of running modes
	private final List<RunningMode> runningModes;

	public UnconstrainedSystemDescription(String resourceId, Date timestamp,
			Date validFrom, Measurable<Duration> allocationDelay,
			List<Timer> timerList, List<RunningMode> runningModes) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.timerList = timerList;
		this.runningModes = runningModes;
	}

}
