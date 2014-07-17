package org.flexiblepower.efi.buffer;

import java.util.Set;

import org.flexiblepower.efi.util.Timer;

public class Transition {
	private final RunningMode toRunningMode;

	/** Timers to be (re)started when this transition is made */
	private Set<Timer> startTimers;

	/** This transition can only be made when all these timers are finished */
	private Set<Timer> blockingTimers;

	// Optional: The costs of a transition
	private Double transitionCosts;

	public Transition(RunningMode toRunningMode, Set<Timer> startTimers,
			Set<Timer> blockingTimers, Double transitionCosts) {
		super();
		this.toRunningMode = toRunningMode;
		this.startTimers = startTimers;
		this.blockingTimers = blockingTimers;
		this.transitionCosts = transitionCosts;
	}
}
