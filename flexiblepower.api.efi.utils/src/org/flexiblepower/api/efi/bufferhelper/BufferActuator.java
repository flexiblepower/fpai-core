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
    private final Map<Integer, Timer> timers = new HashMap<Integer, Timer>();

    public int getCurrentRunningModeId() {
        return currentRunningModeId;
    }

    public void setCurrentRunningModeId(int currentRunningModeId) {
        this.currentRunningModeId = currentRunningModeId;
    }

    public Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> getAllRunningModes() {
        return allRunningModes.values();
    }

    public void setAllRunningModes(Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes) {
        allRunningModes = new HashMap<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : runningModes) {
            allRunningModes.put(r.getId(), r);
        }

        // TODO: don't recreate the timers anew every time
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : allRunningModes.values()) {
            for (Transition tran : r.getTransitions()) {
                // TODO: check that start timers and blocking timers do not overlap, or that it goes well when they do.
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

    private boolean isBlockedOn(Transition transition, Date moment) {
        for (org.flexiblepower.efi.util.Timer t : transition.getBlockingTimers()) {
            if (timers.get(t.getId()).getFinishedAt().after(moment)) {
                return true;
            }
        }
        return false;
    }

    private BufferActuator(int actuatorId, String actuatorLabel, CommoditySet commodities) {
        this.actuatorId = actuatorId;
        this.actuatorLabel = actuatorLabel;
        this.commodities = commodities;
    }

    public BufferActuator(Actuator ac) {
        this(ac.getActuatorId(), ac.getActuatorLabel(), ac.getCommodities());
    }

    /**
     * Gets all the timers of all of the transitions of all of the running modes.
     *
     * @return
     */
    public Map<Integer, Timer> getAllTimers() {
        return timers;
    }

    public CommoditySet getCommodities() {
        return commodities;
    }

    public void updateTimer(int timerId, Date finishedAt) {
        if (getAllTimers().containsKey(timerId))
        {
            getAllTimers().get(timerId).updateFinishedAt(finishedAt);
        }
    }

    public List<Measurable<?>> getPossibleDemands(Date now, double fillLevel) {
        List<Measurable<?>> resultMap = new LinkedList<Measurable<?>>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> rm : getReachableRunningModes(now)) {
            RangeElement<RunningModeBehaviour> element = rm.getValue().getRangeElementForFillLevel(fillLevel);
            resultMap.add(element.getValue().getCommodityConsumption().get(Commodity.ELECTRICITY));
        }
        return resultMap;
    }

    public double getMinimumFillLevel() {
        double lowestBound = Double.MAX_VALUE;
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : allRunningModes.values()) {
            lowestBound = Math.min(lowestBound, r.getValue().getLowerBound());
        }
        return lowestBound;
    }

    public double getMaximumFillLevel() {
        double upperBound = Double.MIN_VALUE;
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> r : allRunningModes.values()) {
            upperBound = Math.max(upperBound, r.getValue().getUpperBound());
        }
        return upperBound;
    }

}
