package org.flexiblepower.efi.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Money;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

/**
 * This class contains the constraints for switching from one {@link RunningMode} to another.
 */
public class Transition {

    /**
     * @param toRunningMode
     *            Identifier the target of this {@link Transition}
     * @return a new {@link Builder} object that can be used to easily create the {@link Transition}
     */
    public static Builder create(int toRunningMode) {
        return new Builder(toRunningMode);
    }

    /**
     * @param toRunningMode
     *            The target of this {@link Transition}
     * @return a new {@link Builder} object that can be used to easily create the {@link Transition}
     */
    public static Builder create(RunningMode<?> toRunningMode) {
        return new Builder(toRunningMode.getId());
    }

    /**
     * This helper class should be used to easily define a {@link Transition}. Use the {@link Transition#create(int)} or
     * {@link Transition#create(RunningMode)} method to get a new instance of this class.
     */
    public static class Builder {
        private final int toRunningMode;
        private final Set<Timer> startTimers;
        private final Set<Timer> blockingTimers;
        private Measurable<Money> transitionCosts;
        private Measurable<Duration> transitionTime;

        Builder(int toRunningMode) {
            this.toRunningMode = toRunningMode;
            startTimers = new HashSet<Timer>();
            blockingTimers = new HashSet<Timer>();
            transitionCosts = Measure.valueOf(0, NonSI.EUROCENT);
            transitionTime = Measure.valueOf(0, SI.SECOND);
        }

        /**
         * @param costs
         *            The costs of making this transition.
         *
         * @return This {@link Builder}
         */
        public Builder setCosts(Measurable<Money> costs) {
            transitionCosts = costs;
            return this;
        }

        /**
         * @param costs
         *            The costs of making this transition in eurocents.
         *
         * @return This {@link Builder}
         */
        public Builder setCosts(double costs) {
            transitionCosts = Measure.valueOf(costs, NonSI.EUROCENT);
            return this;
        }

        /**
         * @param time
         *            The time it takes to make this transition.
         *
         * @return This {@link Builder}
         */
        public Builder setTime(Measurable<Duration> time) {
            transitionTime = time;
            return this;
        }

        /**
         * @param time
         *            The time it takes to make this transition in seconds.
         *
         * @return This {@link Builder}
         */
        public Builder setTime(double time) {
            transitionTime = Measure.valueOf(time, SI.SECOND);
            return this;
        }

        /**
         * Add a {@link Timer} that can block this {@link Transition}.
         *
         * @param timer
         *            The timer that can block this {@link Transition}
         *
         * @return This {@link Builder}
         */
        public Builder isBlockedBy(Timer timer) {
            blockingTimers.add(timer);
            return this;
        }

        /**
         * Add a {@link Timer} that is started when this {@link Transition} is made.
         *
         * @param timer
         *            The timer that is started by this {@link Transition}
         *
         * @return This {@link Builder}
         */
        public Builder starts(Timer timer) {
            startTimers.add(timer);
            return this;
        }

        /**
         * @return A new immutable {@link Transition} object that contains all the elements that have been added until
         *         now.
         */
        public Transition build() {
            return new Transition(toRunningMode,
                                  startTimers,
                                  blockingTimers,
                                  transitionCosts,
                                  transitionTime);
        }
    }

    private final int toRunningMode;

    private final Set<Timer> startTimers;

    private final Set<Timer> blockingTimers;

    private final Measurable<Money> transitionCosts;

    private final Measurable<Duration> transitionTime;

    /**
     * @param toRunningMode
     *            When making this transition this attributes indicates the new {@link RunningMode}
     * @param startTimers
     *            This is a set of zero or more Timer objects. All these timers have to be started when this transition
     *            is made. E.g. when an actuator is being switched on, the “on” timer has to start to make sure that the
     *            actuator will adhere to the correct minimum “on” time before switching off again.
     * @param blockingTimers
     *            This is a set of zero or more Timer objects. All these timers have to be finished before the
     *            transition can be made (this transition is blocked by the timers).
     * @param transitionCosts
     *            It could be that the transition itself will cause wear to the actuator. The deprecation costs
     *            associated with this transition may be expressed by this attribute.
     * @param transitionTime
     *            In some cases a transition will not happen instantly. This attribute specifies the period of time
     *            needed for going from one RunningMode to another (e.g. ramping up or down). It is important to note
     *            that all timers are started at the beginning of the transition and not at its completion.
     */
    public Transition(int toRunningMode,
                      Set<Timer> startTimers,
                      Set<Timer> blockingTimers,
                      Measurable<Money> transitionCosts,
                      Measurable<Duration> transitionTime) {
        this.toRunningMode = toRunningMode;
        this.startTimers = startTimers == null ? Collections.<Timer> emptySet()
                                              : Collections.unmodifiableSet(new HashSet<Timer>(startTimers));
        this.blockingTimers = blockingTimers == null ? Collections.<Timer> emptySet()
                                                    : Collections.unmodifiableSet(new HashSet<Timer>(blockingTimers));
        this.transitionCosts = transitionCosts;
        this.transitionTime = transitionTime == null ? Measure.zero(SI.SECOND) : transitionTime;
    }

    /**
     * @return When making this transition this attributes indicates the new {@link RunningMode}
     */
    public int getToRunningMode() {
        return toRunningMode;
    }

    /**
     * @return This is a set of zero or more Timer objects. All these timers have to be started when this transition is
     *         made. E.g. when an actuator is being switched on, the “on” timer has to start to make sure that the
     *         actuator will adhere to the correct minimum “on” time before switching off again.
     */
    public Set<Timer> getStartTimers() {
        return startTimers;
    }

    /**
     * @return This is a set of zero or more Timer objects. All these timers have to be finished before the transition
     *         can be made.
     */
    public Set<Timer> getBlockingTimers() {
        return blockingTimers;
    }

    /**
     * @return It could be that the transition itself will cause wear to the actuator. The deprecation costs associated
     *         with this transition may be expressed by this attribute.
     */
    public Measurable<Money> getTransitionCosts() {
        return transitionCosts;
    }

    /**
     * @return In some cases a transition will not happen instantly. This attribute specifies the period of time needed
     *         for going from one RunningMode to another (e.g. ramping up or down). It is important to note that all
     *         timers are started at the beginning of the transition and not at its completion.
     */
    public Measurable<Duration> getTransitionTime() {
        return transitionTime;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (31 * (31 *
               blockingTimers.hashCode()
               + startTimers.hashCode())
               + toRunningMode)
               + ((transitionCosts == null) ? 0 : transitionCosts.hashCode()))
               + transitionTime.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Transition other = (Transition) obj;
        if (toRunningMode != other.toRunningMode) {
            return false;
        } else if (!blockingTimers.equals(other.blockingTimers)) {
            return false;
        } else if (!startTimers.equals(other.startTimers)) {
            return false;
        } else if (!transitionTime.equals(other.transitionTime)) {
            return false;
        }

        if (transitionCosts == null) {
            if (other.transitionCosts != null) {
                return false;
            }
        } else if (!transitionCosts.equals(other.transitionCosts)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Transition [toRunningMode=" + toRunningMode
               + ", startTimers="
               + startTimers
               + ", blockingTimers="
               + blockingTimers
               + ", transitionCosts="
               + transitionCosts
               + ", transitionTime="
               + transitionTime
               + "]";
    }
}
