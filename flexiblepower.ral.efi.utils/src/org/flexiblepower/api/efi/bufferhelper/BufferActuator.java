package org.flexiblepower.api.efi.bufferhelper;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;

import org.flexiblepower.api.efi.commonhelper.TimerModel;
import org.flexiblepower.efi.buffer.Actuator;
import org.flexiblepower.efi.buffer.RunningModeBehaviour;
import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.FillLevelFunction.RangeElement;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.efi.util.Transition;
import org.flexiblepower.ral.values.Commodity;
import org.flexiblepower.ral.values.CommoditySet;

/**
 * The BufferActuator that integrates updated EFI messages and provides additional methods.
 *
 * @param <Q>
 *            The quantity type of the buffer
 */
public class BufferActuator<Q extends Quantity> {
    private final int actuatorId;
    private final String actuatorLabel;
    private final CommoditySet commodities;
    private int currentRunningModeId;
    private Map<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>> allRunningModes = new HashMap<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
    private Map<Integer, TimerModel> timers = new HashMap<Integer, TimerModel>();
    private final Buffer<Q> parentBuffer;

    /**
     * Gets the identifier of the current running mode.
     *
     * @return The identifier of the current running mode.
     */
    public int getCurrentRunningModeId() {
        return currentRunningModeId;
    }

    /**
     * Sets the current running mode.
     *
     * @param currentRunningModeId
     *
     * @throws IllegalArgumentException
     *             When running mode id is not valid.
     */
    public void setCurrentRunningModeId(int currentRunningModeId) throws IllegalArgumentException {
        if (allRunningModes.containsKey(currentRunningModeId)) {
            this.currentRunningModeId = currentRunningModeId;
        }
        else {
            throw new IllegalArgumentException("Can't set running mode to non-existing running mode.");
        }
    }

    /**
     * Gets all RunningModes of this actuator.
     *
     * @return Gets all RunningModes of this actuator.
     */
    public Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> getAllRunningModes() {
        return allRunningModes.values();
    }

    /**
     * Renews the set of RunningModes of this Actuator.
     *
     * @param runningModes
     */
    protected void setAllRunningModes(Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes) {
        allRunningModes = new HashMap<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : runningModes) {
            allRunningModes.put(r.getId(), r);
        }

        timers = new HashMap<Integer, TimerModel>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : allRunningModes.values()) {
            for (Transition tran : r.getTransitions()) {
                for (org.flexiblepower.efi.util.Timer blockingTimer : tran.getBlockingTimers()) {
                    timers.put(blockingTimer.getId(), new TimerModel(blockingTimer));
                }
                for (org.flexiblepower.efi.util.Timer startTimer : tran.getStartTimers()) {
                    timers.put(startTimer.getId(), new TimerModel(startTimer));
                }
            }
        }
    }

    /**
     * Returns whether the RunningMode id is valid for this actuator.
     *
     * @param rmId
     *            the Id of the RunningMode.
     * @return True when the RunningMode exists for this actuator, false otherwise.
     */
    public boolean hasRunningMode(int rmId) {
        if (allRunningModes == null)
        {
            return false;
        }
        return (allRunningModes.containsKey(rmId));
    }

    /**
     * Returns the reachableRunning modes including the current one, if it may stay in it. Returns the empty set if it
     * has not yet received enough information(All EFI messages including BufferStateUpdate message).
     *
     * @param now
     *            The current time.
     * @return The reachable running modes including the current one.
     *
     * @throws IllegalArgumentException
     *             When an unknown running mode id is returned from the reachable running modes.
     */
    public Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>>
            getReachableRunningModes(Date now) throws IllegalArgumentException {
        Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> rmSet = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        for (int rmId : getReachableRunningModeIds(now)) {
            if (!allRunningModes.containsKey(rmId))
            {
                throw new IllegalArgumentException("Running Mode Id is not known.");
            }
            rmSet.add(allRunningModes.get(rmId));
        }
        return rmSet;
    }

    /**
     * Returns the reachableRunning mode ids including the current one, if it may stay in it. Returns the empty set if
     * it has not yet received enough information (All EFI messages including BufferStateUpdate message).
     *
     * @param now
     *            The current time.
     * @return The reachable running mode ids including the current one.
     */
    public Set<Integer> getReachableRunningModeIds(Date now) {
        Set<Integer> targets = new HashSet<Integer>();
        if (allRunningModes == null || allRunningModes.get(currentRunningModeId) == null)
        {
            // Device is not in a valid state. No reachable running modes.
            return targets;
        }
        for (Transition transition : allRunningModes.get(currentRunningModeId).getTransitions()) {
            // Check for timers that block this transition, over and undercharge and that the current state of charge is
            // within the valid range of the target running mode.
            if (!isBlockedAt(transition, now) && !willOverOrUndercharge(transition, now)
                && !allRunningModes.get(transition.getToRunningMode())
                                   .getValue()
                                   .isOutsideOfRange(parentBuffer.getCurrentFillLevel()
                                                                 .doubleValue(parentBuffer.getUnit()))) {
                targets.add(transition.getToRunningMode());
            }
        }
        targets.add(currentRunningModeId);
        return targets;
    }

    /**
     * This function is not implemented yet, but will provide an estimate of whether a transition (die to
     * timers/transitions) will lead to an over or undercharge of the buffer.
     *
     * @param transition
     *            The transition to be checked.
     * @param now
     *            The moment for which the transition should be checked.
     * @return Whether the transition is impossible due to timers follow-up transitions. Always returns false for now.
     */
    private boolean willOverOrUndercharge(Transition transition, Date now) {
        // TODO: This is a complex problem... Discussion Wilco JP 17 Oct 2014
        // If I make this transition will I overcharge the buffer.
        // Check the blocking timers this transition starts
        // Check usage and leakage as well...
        return false;
    }

    /**
     * Checks all timers to see if a transition is blocked on the moment.
     *
     * @param transition
     *            The transition of this actuator that you are interested in.
     * @param moment
     *            The moment at which the possibility is or is not blocked.
     * @return True if the transition is blocked, false if it is not.
     */
    private boolean isBlockedAt(Transition transition, Date moment) {
        for (org.flexiblepower.efi.util.Timer t : transition.getBlockingTimers()) {
            TimerModel at = timers.get(t.getId());
            if (at.isBlockingAt(moment)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Constructs a BufferActuator from the Actuator information in a message. The BufferActuator persists so that any
     * updates to the actuator can be integrated in this single object.
     *
     * @param ac
     *            The Actuator's initial information.
     * @param parentBuffer
     *            The Parent Buffer (to gain access to the fill level).
     */
    public BufferActuator(Actuator ac, Buffer<Q> parentBuffer) {
        this(ac.getActuatorId(), ac.getActuatorLabel(), ac.getCommodities(), parentBuffer);
    }

    /**
     * Constructs an actuator from the initially available information.
     *
     * @param actuatorId
     *            The identifier that is unique within this buffer.
     * @param actuatorLabel
     *            The friendly label of this actuator.
     * @param commodities
     *            The supported commodities of this actuator.
     */
    private BufferActuator(int actuatorId, String actuatorLabel, CommoditySet commodities, Buffer<Q> buffer) {
        this.actuatorId = actuatorId;
        this.actuatorLabel = actuatorLabel;
        this.commodities = commodities;
        parentBuffer = buffer;
    }

    /**
     * Gets all the timers of all of the transitions of all of the running modes.
     *
     * @return A Map of timer id and timers.
     */
    public Map<Integer, TimerModel> getAllTimers() {
        return timers;
    }

    /**
     * Returns the commodities that this actuator is able to use as either input or output.
     *
     * @return The set of commodities and whether they are supported by this actuator.
     */
    public CommoditySet getSupportedCommodities() {
        return commodities;
    }

    /**
     * Upon receiving an update, the end time of the given timer is updated.
     *
     * @param timerId
     *            The id of the timer.
     * @param finishedAt
     *            The new finishedAt time that overwrites the old one.
     */
    public void updateTimer(int timerId, Date finishedAt) {
        if (getAllTimers().containsKey(timerId))
        {
            getAllTimers().get(timerId).updateFinishedAt(finishedAt);
        }
    }

    /**
     * Gets the electrical demands of all reachable RunningModes at this moment including the current one, given this
     * fill level of the buffer. It returns an empty list when there has not been a state update.
     *
     * @param moment
     *            The moment of interest.
     * @param fillLevel
     *            The buffer's current fill level expressed as a value where 0 is the minimum and 1 is the maximum. Its
     *            unit is the agreed upon unit.
     * @return An unordered list of the possible electricity consumption demands of the RunningModes, possibly including
     *         running modes with the same power demand. For fill levels outside the defined minimum and maximum range,
     *         the minimum and maximum value is returned.
     *
     * @throws IllegalArgumentException
     *             When a FillLevelFunction of a reachable state has no range elements.
     */
    public List<Measurable<Power>> getPossibleDemands(Date moment, double fillLevel) throws IllegalArgumentException {
        List<Measurable<Power>> resultMap = new LinkedList<Measurable<Power>>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> rm : getReachableRunningModes(moment)) {

            // Check whether the Running Mode is not empty.
            if (rm.getValue().isEmpty()) {
                throw new IllegalArgumentException("FillLevelFunction was not expected to be empty.");
            }

            RangeElement<RunningModeBehaviour> element;
            if (rm.getValue().isAboveMaximum(fillLevel))
            {
                // Buffer fill level is above the maximum defined value.
                element = rm.getValue().get(rm.getValue().size() - 1);
            } else if (rm.getValue().isBelowMinimum(fillLevel))
            {
                // Buffer fill level is below minimum defined value.
                element = rm.getValue().get(0);
            } else {
                // The fill level is in good range.
                element = rm.getValue().getRangeElementForFillLevel(fillLevel);
            }
            resultMap.add(element.getValue().getCommodityConsumption().get(Commodity.ELECTRICITY));
        }
        return resultMap;
    }

    /**
     * Gets the minimum fill level of the buffer actuator expressed in the agreed upon unit.
     *
     * @return The minimum fill level of the buffer actuator.
     *
     * @throws IllegalStateException
     *             Thrown when no running mode is known yet and a minimum fill level is requested.
     */
    public double getMinimumFillLevel() throws IllegalStateException {
        double lowestBound = Double.MAX_VALUE;
        if (allRunningModes.isEmpty()) {
            throw new IllegalStateException("Cannot give minimum fill level, because it is not known yet.");
        }
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : allRunningModes.values()) {
            lowestBound = Math.min(lowestBound, r.getValue().getLowerBound());
        }
        return lowestBound;
    }

    /**
     * Gets the maximum fill level of the buffer actuator expressed in the agreed upon unit.
     *
     * @return The maximum fill level of the buffer actuator.
     * @throws IllegalStateException
     *             Thrown when no running mode is known yet and a maximum fill level is requested.
     */
    public double getMaximumFillLevel() throws IllegalStateException {
        double upperBound = Double.MIN_VALUE;
        if (allRunningModes.isEmpty()) {
            throw new IllegalStateException("Cannot give minimum fill level, because it is not known yet.");
        }
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : allRunningModes.values()) {
            upperBound = Math.max(upperBound, r.getValue().getUpperBound());
        }
        return upperBound;
    }

    /**
     * Gets the identifier of this actuator.
     *
     * @return The identifier of this actuator.
     */
    public int getActuatorId() {
        return actuatorId;
    }

    /**
     * Gets the friendly label for this actuator.
     *
     * @return The string containing the friendly name for this actuator.
     */
    public String getActuatorLabel() {
        return actuatorLabel;
    }
}
