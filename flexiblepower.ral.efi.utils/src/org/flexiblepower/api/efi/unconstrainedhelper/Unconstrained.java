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
import org.flexiblepower.efi.unconstrained.UnconstrainedRegistration;
import org.flexiblepower.efi.unconstrained.UnconstrainedStateUpdate;
import org.flexiblepower.efi.unconstrained.UnconstrainedSystemDescription;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.efi.util.TimerUpdate;
import org.flexiblepower.efi.util.Transition;
import org.flexiblepower.ral.values.Commodity;
import org.flexiblepower.ral.values.CommoditySet;

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
     * Constructs a Unconstrained object that keeps the state of the Unconstrained device and provides helper function
     * for the matcher, like which states are reachable.
     *
     * @param registration
     *            The complete and correct initial registration message.
     */
    public Unconstrained(UnconstrainedRegistration registration) {
        this(registration.getResourceId(), registration.getSupportedCommodities());
    }

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
        hasReceivedStateUpdate = false;
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
     *
     * @throws IllegalArgumentException
     *             StateUpdate contains not known RunningMode.
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
     *
     * @throws IllegalArgumentException
     *             The RunningModeId is not known.
     */
    public Collection<RunningMode<RunningModeBehaviour>>
            getReachableRunningModes(Date now) throws IllegalArgumentException {
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
     * Gets the electrical demands of all reachable RunningModes at this moment including the current one. It returns an
     * empty list when there has not been a state update.
     *
     * @param moment
     *            The moment of interest.
     * @return An unordered list of the possible electricity consumption demands of the RunningModes, possibly including
     *         running modes with the same power demand.
     *
     * @throws IllegalArgumentException
     *             When a RunningMode is empty.
     */
    public List<Measurable<Power>> getPossibleDemands(Date moment) {
        List<Measurable<Power>> resultList = new LinkedList<Measurable<Power>>();
        for (RunningMode<RunningModeBehaviour> rm : getReachableRunningModes(moment)) {
            // Check whether the Running Mode is not empty.
            if (rm.getValue() == null) {
                throw new IllegalArgumentException("RunningMode was not expected to be empty.");
            }
            resultList.add(rm.getValue().getCommodityConsumption().get(Commodity.ELECTRICITY));
        }
        return resultList;
    }

    /**
     * Gets the electrical demands of all reachable RunningModes at this moment including the current one. It returns an
     * empty list when there has not been a state update.
     *
     * @param moment
     *            The moment of interest.
     * @param fillLevel
     *            The current fillLevel
     * @return An unordered list of the possible electricity consumption demands of the RunningModes, possibly including
     *         running modes with the same power demand.
     *
     * @throws IllegalArgumentException
     *             When a RunningMode is empty.
     */
    public List<Measurable<Power>> getPossibleDemands(Date moment, double fillLevel) {
        return getPossibleDemands(moment);
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
     *
     * @throws IllegalStateException
     *
     */
    private boolean isBlockedAt(Transition transition, Date moment) throws IllegalStateException {
        if (!hasReceivedSystemDescription) {
            throw new IllegalStateException("Can not call this method when no sys description is known yet.");
        }
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

    /**
     * Indicates whether this Unconstrained has seen a SystemDescription yet.
     *
     * @return Whether the Unconstrained has received a valid SystemDescription.
     */
    public boolean hasReceivedStateUpdate() {
        return hasReceivedStateUpdate;
    }

    /**
     * Indicates whether this Unconstrained device has seen a SystemDescription yet.
     *
     * @return Whether the Unconstrained has received a valid StateUpdate.
     */
    public boolean hasReceivedSystemDescription() {
        return hasReceivedSystemDescription;
    }

    /**
     * The current running mode.
     *
     * @return The Id (integer) of the current running mode.
     */
    public int getCurrentRunningModeId() {
        return currentRunningModeId;
    }

    /**
     * The ResourceId of this Unconstrained device
     *
     * @return The ResourceId of this Unconstrained device.
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * @return The supported commodities by this device.
     */
    public CommoditySet getSupportedCommodities() {
        return supportedCommodities;
    }
}
