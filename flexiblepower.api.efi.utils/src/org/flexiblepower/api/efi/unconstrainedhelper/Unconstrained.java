package org.flexiblepower.api.efi.unconstrainedhelper;

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

import org.flexiblepower.api.efi.commonhelper.TimerModel;
import org.flexiblepower.efi.unconstrained.RunningModeBehaviour;
import org.flexiblepower.efi.unconstrained.UnconstrainedStateUpdate;
import org.flexiblepower.efi.unconstrained.UnconstrainedSystemDescription;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.efi.util.TimerUpdate;
import org.flexiblepower.efi.util.Transition;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommoditySet;

/**
 * This class contains helper functions to process the Unconstrained EFI messages (registration, update and system
 * description) and to provide shortcuts for the Controller/Energy App to determine which RunningModes (and commodity
 * demand values) are reachable given the current state of the device.
 */
public class Unconstrained {
    private final String resourceId;
    private final CommoditySet supportedCommodities;

    private Map<Integer, RunningMode<RunningModeBehaviour>> allRunningModes = new HashMap<Integer, RunningMode<RunningModeBehaviour>>();
    private Map<Integer, TimerModel> timers;
    private boolean hasReceivedSystemDescription = false;
    private boolean hasReceivedStateUpdate = false;
    private int currentRunningModeId;

    /**
     * Private constructor for the unconstrained model.
     *
     * @param resourceId
     *            The resource id of the unconstrained device
     * @param supportedCommodities
     *            The set of supported commodities.
     */
    private Unconstrained(String resourceId, CommoditySet supportedCommodities) {
        this.resourceId = resourceId;
        this.supportedCommodities = supportedCommodities;
    }

    /**
     * Takes a system description and inserts the running modes and the timers.
     *
     * @param description
     *            The description message with Timers and RunningModes.
     */
    public void processSystemDescription(UnconstrainedSystemDescription description) {
        allRunningModes = new HashMap<Integer, RunningMode<RunningModeBehaviour>>();
        for (RunningMode<RunningModeBehaviour> rm : description.getRunningModes())
        {
            allRunningModes.put(rm.getId(), rm);
        }

        timers = new HashMap<Integer, TimerModel>();
        for (RunningMode<RunningModeBehaviour> r : allRunningModes.values()) {
            for (Transition tran : r.getTransitions()) {
                for (org.flexiblepower.efi.util.Timer blockingTimer : tran.getBlockingTimers()) {
                    timers.put(blockingTimer.getId(), new TimerModel(blockingTimer));
                }
                for (org.flexiblepower.efi.util.Timer startTimer : tran.getStartTimers()) {
                    timers.put(startTimer.getId(), new TimerModel(startTimer));
                }
            }
        }
        hasReceivedSystemDescription = true;
    }

    /**
     * Updates the current running mode and the timers.
     *
     * @param stateUpdate
     *            Contains an update for the timers that have a new finishedAt time. No timer information means no
     *            updated information and the previously known finishedAt remains true.
     */
    public void processStateUpdate(UnconstrainedStateUpdate stateUpdate) {
        if (!hasReceivedSystemDescription) {
            return;
        }
        if (!allRunningModes.containsKey(stateUpdate.getCurrentRunningModeId()))
        {
            throw new IllegalArgumentException("StateUpdate message has a RunningMode that is unknown.");
        }
        currentRunningModeId = stateUpdate.getCurrentRunningModeId();

        for (TimerUpdate timerUpdate : stateUpdate.getTimerUpdates())
        {
            updateTimer(timerUpdate.getTimerId(), timerUpdate.getFinishedAt());
        }
        hasReceivedStateUpdate = true;
    }

    /**
     * Returns the reachableRunning modes including the current one, if it may stay in it. Returns the empty set if it
     * has not yet received enough information(All EFI messages including StateUpdate message).
     *
     * @param now
     *            The current time.
     * @return The reachable running modes including the current one.
     */
    public Collection<RunningMode<RunningModeBehaviour>> getReachableRunningModes(Date now) {
        Set<RunningMode<RunningModeBehaviour>> rmSet = new HashSet<RunningMode<RunningModeBehaviour>>();
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
     * it has not yet received enough information (All EFI messages including StateUpdate message).
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
            // Check for timers that block this transition.
            if (!isBlockedAt(transition, now) && !willOverOrUndercharge(transition, now)) {
                targets.add(transition.getToRunningMode());
            }
        }
        targets.add(currentRunningModeId);
        return targets;
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
    public List<Measurable<Power>> getPossibleDemands(Date moment, double fillLevel) {
        List<Measurable<Power>> resultMap = new LinkedList<Measurable<Power>>();
        for (RunningMode<RunningModeBehaviour> rm : getReachableRunningModes(moment)) {
            // Check whether the Running Mode is not empty.
            if (rm.getValue() == null) {
                throw new IllegalArgumentException("RunningMode was not expected to be empty.");
            }

            resultMap.add(rm.getValue().getCommodityConsumption().get(Commodity.ELECTRICITY));
        }
        return resultMap;
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
     * Upon receiving an update, the end time of the given timer is updated.
     *
     * @param timerId
     *            The id of the timer.
     * @param finishedAt
     *            The new finishedAt time that overwrites the old one.
     */
    public void updateTimer(int timerId, Date finishedAt) {
        if (timers.containsKey(timerId))
        {
            timers.get(timerId).updateFinishedAt(finishedAt);
        }
    }

    /**
     * Returns whether this Unconstrained device has this running mode or not.
     *
     * @param rmId
     *            The Id of the RunningMode
     * @return True when the RunningMode is known to this Unconstrained device, false otherwise.
     */
    public boolean hasRunningMode(int rmId) {
        if (allRunningModes == null) {
            return false;
        }
        return (allRunningModes.containsKey(rmId));
    }
}
