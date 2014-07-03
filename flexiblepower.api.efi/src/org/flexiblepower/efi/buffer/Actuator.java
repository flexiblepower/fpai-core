package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.List;

public class Actuator {
	private int id;

	// Timers associated with this actuator
	public List<Timer> timerList;

	// List of running modes
	private List<RunningMode> runningModes;
	private RunningMode currentRunningMode;
	private Date startTimeCurrentRunningMode;

	// Defines possible Transitions
	private final List<Transition> transitions;

	// Constructor
	public Actuator(List<RunningMode> runningModes,
			RunningMode currentRunningMode, Date timeInCurrentRunningMode,
			List<Transition> transitions) {

		this.runningModes = runningModes;
		this.currentRunningMode = currentRunningMode;
		this.startTimeCurrentRunningMode = timeInCurrentRunningMode;

		this.transitions = transitions;
	}
}
