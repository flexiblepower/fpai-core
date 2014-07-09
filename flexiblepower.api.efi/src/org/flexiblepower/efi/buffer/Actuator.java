package org.flexiblepower.efi.buffer;

import java.io.Serializable;
import java.util.List;

public class Actuator implements Serializable {

	private static final long serialVersionUID = -7888970166563077855L;

	private final int id;

	// Timers associated with this actuator
	private final List<Timer> timerList;

	// List of running modes
	private final List<RunningMode> runningModes;

	public Actuator(int id, List<Timer> timerList,
			List<RunningMode> runningModes) {
		super();
		this.id = id;
		this.timerList = timerList;
		this.runningModes = runningModes;
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

}
