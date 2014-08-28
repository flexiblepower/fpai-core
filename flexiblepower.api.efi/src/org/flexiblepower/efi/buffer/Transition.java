package org.flexiblepower.efi.buffer;

import java.util.HashSet;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Money;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.flexiblepower.efi.util.Timer;

public class Transition {
    public static Builder create(int toRunningMode) {
        return new Builder(toRunningMode);
    }

    public static Builder create(RunningMode toRunningMode) {
        return new Builder(toRunningMode.getId());
    }

    public static class Builder {
        private final int toRunningMode;

        /** Timers to be (re)started when this transition is made */
        private final Set<Timer> startTimers;

        /** This transition can only be made when all these timers are finished */
        private final Set<Timer> blockingTimers;

        /** Optional: The costs of a transition */
        private Measurable<Money> transitionCosts;

        /** The time duration it takes for a transition */
        private Measurable<Duration> transitionTime;

        Builder(int toRunningMode) {
            this.toRunningMode = toRunningMode;
            startTimers = new HashSet<Timer>();
            blockingTimers = new HashSet<Timer>();
            transitionCosts = Measure.valueOf(0, NonSI.EUROCENT);
            transitionTime = Measure.valueOf(0, SI.SECOND);
        }

        public Builder setCosts(Measurable<Money> costs) {
            transitionCosts = costs;
            return this;
        }

        /** Set the costs in eurocents */
        public Builder setCosts(double costs) {
            transitionCosts = Measure.valueOf(costs, NonSI.EUROCENT);
            return this;
        }

        public Builder setTime(Measurable<Duration> time) {
            transitionTime = time;
            return this;
        }

        /** Set the time in seconds */
        public Builder setTime(double time) {
            transitionTime = Measure.valueOf(time, SI.SECOND);
            return this;
        }

        public Builder blocks(Timer timer) {
            blockingTimers.add(timer);
            return this;
        }

        public Builder starts(Timer timer) {
            startTimers.add(timer);
            return this;
        }

        public Transition build() {
            return new Transition(
                                  toRunningMode,
                                  startTimers,
                                  blockingTimers,
                                  transitionCosts,
                                  transitionTime);
        }
    }

    private final int toRunningMode;

    /** Timers to be (re)started when this transition is made */
    private final Set<Timer> startTimers;

    /** This transition can only be made when all these timers are finished */
    private final Set<Timer> blockingTimers;

    /** Optional: The costs of a transition */
    private final Measurable<Money> transitionCosts;

    /** The time duration it takes for a transition */
    private final Measurable<Duration> transitionTime;

    public Transition(int toRunningMode,
                      Set<Timer> startTimers,
                      Set<Timer> blockingTimers,
                      Measurable<Money> transitionCosts,
                      Measurable<Duration> transitionTime) {
        this.toRunningMode = toRunningMode;
        this.startTimers = startTimers;
        this.blockingTimers = blockingTimers;
        this.transitionCosts = transitionCosts;
        this.transitionTime = transitionTime;
    }

    public int getToRunningMode() {
        return toRunningMode;
    }

    public Set<Timer> getStartTimers() {
        return startTimers;
    }

    public Set<Timer> getBlockingTimers() {
        return blockingTimers;
    }

    public Measurable<Money> getTransitionCosts() {
        return transitionCosts;
    }

    public Measurable<Duration> getTransitionTime() {
        return transitionTime;
    }
}
