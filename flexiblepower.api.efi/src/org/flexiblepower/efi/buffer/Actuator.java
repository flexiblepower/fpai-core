package org.flexiblepower.efi.buffer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Actuator implements Serializable {

	private static final long serialVersionUID = -7888970166563077855L;

	private final int id;

	// Timers associated with this actuator
	private final List<Timer> timerList;

	// List of running modes
	private final List<RunningMode> runningModes;
	private final RunningMode currentRunningMode;
	private final Date startTimeCurrentRunningMode;

	// Defines possible Transitions
	private final Set<Transition> transitions;

	public Actuator(int id, List<Timer> timerList,
			List<RunningMode> runningModes, RunningMode currentRunningMode,
			Date startTimeCurrentRunningMode, Set<Transition> transitions) {
		super();
		this.id = id;
		this.timerList = timerList;
		this.runningModes = runningModes;
		this.currentRunningMode = currentRunningMode;
		this.startTimeCurrentRunningMode = startTimeCurrentRunningMode;
		this.transitions = transitions;
	}

	public int getId() {
		return id;
	}

	public List<Timer> getTimerList() {
		return timerList;
	}

	public List<RunningMode> getRunningModes() {
		return runningModes;
	}

	public RunningMode getCurrentRunningMode() {
		return currentRunningMode;
	}

	public Date getStartTimeCurrentRunningMode() {
		return startTimeCurrentRunningMode;
	}

	public Set<Transition> getTransitions() {
		return transitions;
	}

}
