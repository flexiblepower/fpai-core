package org.flexiblepower.api.efi.bufferhelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.measure.Measurable;

import org.flexiblepower.efi.buffer.Actuator;
import org.flexiblepower.efi.buffer.RunningModeBehaviour;
import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.FillLevelFunction.RangeElement;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.efi.util.Transition;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommoditySet;

public class BufferActuator {
    private final int actuatorId;
    private final String actuatorLabel;
    private final CommoditySet commodities;
    private int currentRunningModeId;
    private Map<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>> allRunningModes = new HashMap<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
    private Map<Integer, Timer> timers = new HashMap<Integer, Timer>();

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
     */
    public void setCurrentRunningModeId(int currentRunningModeId) {
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

        timers = new HashMap<Integer, Timer>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : allRunningModes.values()) {
            for (Transition tran : r.getTransitions()) {
                for (org.flexiblepower.efi.util.Timer blockingTimer : tran.getBlockingTimers()) {
                    timers.put(blockingTimer.getId(), new Timer(blockingTimer));
                }
                for (org.flexiblepower.efi.util.Timer startTimer : tran.getStartTimers()) {
                    timers.put(startTimer.getId(), new Timer(startTimer));
                }
            }
        }
    }

    /**
     * Returns the reachableRunning modes including the current one, if it may stay in it.
     *
     * @param now
     *            The current time.
     * @return The reachable running modes including the current one.
     */
    public Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> getReachableRunningModes(Date now) {
        Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> targets = new ArrayList<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        for (Transition transition : allRunningModes.get(currentRunningModeId).getTransitions()) {
            // Check for timers that block this transition.
            if (!isBlockedOn(transition, now)) {
                targets.add(allRunningModes.get(transition.getToRunningMode()));
            }
        }
        targets.add(allRunningModes.get(currentRunningModeId));
        return targets;
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
    private boolean isBlockedOn(Transition transition, Date moment) {
        for (org.flexiblepower.efi.util.Timer t : transition.getBlockingTimers()) {
            if (timers.get(t.getId()).getFinishedAt().after(moment)) {
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
     */
    public BufferActuator(Actuator ac) {
        this(ac.getActuatorId(), ac.getActuatorLabel(), ac.getCommodities());
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
    private BufferActuator(int actuatorId, String actuatorLabel, CommoditySet commodities) {
        this.actuatorId = actuatorId;
        this.actuatorLabel = actuatorLabel;
        this.commodities = commodities;
    }

    /**
     * Gets all the timers of all of the transitions of all of the running modes.
     *
     * @return A Map of timer id and timers.
     */
    public Map<Integer, Timer> getAllTimers() {
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
     * Gets the possible demands of all reachable RunningModes at this moment, given this fill level of the buffer.
     *
     * @param moment
     *            The moment of interest.
     * @param fillLevel
     *            The buffer's current fill level expressed as a value where 0 is the minimum and 1 is the maximum. Its
     *            unit is the agreed upon unit.
     * @return An unordered list of the possible demands, possibly including duplicates.
     */
    public List<Measurable<?>> getPossibleDemands(Date moment, double fillLevel) {
        List<Measurable<?>> resultMap = new LinkedList<Measurable<?>>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> rm : getReachableRunningModes(moment)) {
            RangeElement<RunningModeBehaviour> element = rm.getValue().getRangeElementForFillLevel(fillLevel);
            resultMap.add(element.getValue().getCommodityConsumption().get(Commodity.ELECTRICITY));
        }
        return resultMap;
    }

    /**
     * Gets the minimum fill level of the buffer expressed in the agreed upon unit.
     *
     * @return The minimum fill level of the buffer.
     */
    public double getMinimumFillLevel() {
        double lowestBound = Double.MAX_VALUE;
        if (allRunningModes.isEmpty()) {

        }
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : allRunningModes.values()) {
            lowestBound = Math.min(lowestBound, r.getValue().getLowerBound());
        }
        return lowestBound;
    }

    /**
     * Gets the maximum fill level of the buffer expressed in the agreed upon unit.
     *
     * @return The maximum fill level of the buffer.
     */
    public double getMaximumFillLevel() {
        double upperBound = Double.MIN_VALUE;
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
