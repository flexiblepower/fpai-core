package org.flexiblepower.efi.buffer;

import java.util.Set;

import org.flexiblepower.efi.util.Timer;
import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class Transition {
	private final RunningMode toRunningMode;

	/** Timers to be (re)started when this transition is made */
	private final Set<Timer> startTimers;

	/** This transition can only be made when all these timers are finished */
	private final Set<Timer> blockingTimers;

	// Optional: The costs of a transition
	private final Double transitionCosts;

	/** The time duration it takes for a transition */
	private Measurable<Duration> transitionTime;

	public Transition(RunningMode toRunningMode, Set<Timer> startTimers,
			Set<Timer> blockingTimers, Double transitionCosts) {
		super();
		this.toRunningMode = toRunningMode;
		this.startTimers = startTimers;
		this.blockingTimers = blockingTimers;
		this.transitionCosts = transitionCosts;
	}
}
