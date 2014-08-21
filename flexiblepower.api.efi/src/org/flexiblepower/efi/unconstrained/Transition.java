package org.flexiblepower.efi.unconstrained;

import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Money;

import org.flexiblepower.efi.util.Timer;

public class Transition {
    private final RunningMode toRunningMode;

    /** Timers to be (re)started when this transition is made */
    private final Set<Timer> startTimers;

    /** This transition can only be made when all these timers are finished */
    private final Set<Timer> blockingTimers;

    // Optional: The costs of a transition
    private final Measurable<Money> transitionCosts;

    /** The time duration it takes for a transition */
    private final Measurable<Duration> transitionTime;

    public Transition(RunningMode toRunningMode,
                      Set<Timer> startTimers,
                      Set<Timer> blockingTimers,
                      Measurable<Money> transitionCosts,
                      Measurable<Duration> transitionTime) {
        super();
        this.toRunningMode = toRunningMode;
        this.startTimers = startTimers;
        this.blockingTimers = blockingTimers;
        this.transitionCosts = transitionCosts;
        this.transitionTime = transitionTime;
    }
}
