package org.flexiblepower.api.efi.bufferhelper;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.measure.Measurable;

import org.flexiblepower.efi.buffer.BufferRegistration.ActuatorCapabilities;
import org.flexiblepower.efi.buffer.RunningMode;
import org.flexiblepower.efi.buffer.RunningMode.RunningModeRangeElement;
import org.flexiblepower.efi.buffer.Transition;
import org.flexiblepower.efi.util.Timer;
import org.flexiblepower.rai.values.Commodity;

public class BufferActuator {

    private final int actuatorId;
    private final String actuatorLabel;
    private final Commodity.Set commodities;

    private Map<Integer, RunningMode> allRunningModes;
    private int currentRunningModeId;

    public int getCurrentRunningModeId() {
        return currentRunningModeId;
    }

    public void setCurrentRunningModeId(int currentRunningModeId) {
        this.currentRunningModeId = currentRunningModeId;
    }

    public Map<Integer, RunningMode> getAllRunningModes() {
        return allRunningModes;
    }

    public void setAllRunningModes(List<RunningMode> runningModeList) {
        allRunningModes = new HashMap<Integer, RunningMode>();
        for (RunningMode r : runningModeList) {
            allRunningModes.put(r.getId(), r);
        }
    }

    public Map<Integer, RunningMode> getReachableRunningModes(Date now) {
        Map<Integer, RunningMode> targets = new HashMap<Integer, RunningMode>();
        for (Transition i : allRunningModes.get(currentRunningModeId).getPossibleTransitions()) {
            // Check for timers that block this transition.
            if (!i.isBlockedOn(now)) {
                targets.put(i.getToRunningMode().getId(), i.getToRunningMode());
            }
        }
        return targets;
    }

    private BufferActuator(int actuatorId, String actuatorLabel, Commodity.Set commodities) {
        this.actuatorId = actuatorId;
        this.actuatorLabel = actuatorLabel;
        this.commodities = commodities;
    }

    public BufferActuator(ActuatorCapabilities ac) {
        this(ac.getActuatorId(), ac.getActuatorLabel(), ac.getCommodities());
    }

    /**
     * Gets all the timers of all of the transitions of all of the running modes.
     *
     * @return
     */
    public Map<Integer, Timer> getAllTimers() {
        Map<Integer, Timer> timerList = new HashMap<Integer, Timer>();
        for (RunningMode r : allRunningModes.values()) {
            for (Transition tran : r.getPossibleTransitions()) {
                // TODO: check that start timers and blocking timers do not overlap, or that it goes well when they do.
                for (Timer blockingTimer : tran.getBlockingTimers()) {
                    timerList.put(blockingTimer.getId(), blockingTimer);
                }
                for (Timer startTimer : tran.getStartTimers()) {
                    timerList.put(startTimer.getId(), startTimer);
                }
            }
        }
        return timerList;
    }

    public Commodity.Set getCommodities() {
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
        for (RunningMode rm : getReachableRunningModes(now).values()) {
            // TODO: Check cast
            RunningModeRangeElement rmre = (RunningModeRangeElement) (rm.getRangeElementForFillLevel(fillLevel));
            resultMap.add(rmre.getCommodityConsumption().get(Commodity.ELECTRICITY));
        }
        return resultMap;
    }
}
