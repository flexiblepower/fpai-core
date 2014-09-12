package org.flexiblepower.api.efi.bufferhelper;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.flexiblepower.efi.buffer.Actuator;
import org.flexiblepower.efi.buffer.ActuatorBehaviour;
import org.flexiblepower.efi.buffer.ActuatorUpdate;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferStateUpdate;
import org.flexiblepower.efi.buffer.BufferSystemDescription;
import org.flexiblepower.efi.buffer.LeakageRate;
import org.flexiblepower.efi.buffer.RunningModeBehaviour;
import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.efi.util.TimerUpdate;
import org.flexiblepower.rai.values.Commodity;

/**
 * Currently this class can process registration, system description and update messages.
 *
 * @author wijbengajp
 *
 */
public class Buffer<Q extends Quantity> {

    private final String resourceId;
    private final String fillLevelLabel;
    private final Unit<Q> fillLevelUnit;
    private final Measurable<Duration> allocationDelay;
    private final Map<Integer, BufferActuator> actuators;
    private FillLevelFunction<LeakageRate> leakageFunction;
    private Measurable<Q> currentFillLevel;

    /** A Buffer may only be constructed from a complete BufferRegistration message. */
    public Buffer(BufferRegistration<Q> br) {
        this(br.getResourceId(),
             br.getFillLevelLabel(),
             br.getFillLevelUnit(),
             br.getAllocationDelay(),
             br.getActuators());
    }

    private Buffer(String resourceId,
                   String getxLabel,
                   Unit<Q> getxUnit,
                   Measurable<Duration> allocationDelay,
                   Collection<Actuator> actuatorCapabilities) {
        this.resourceId = resourceId;
        fillLevelLabel = getxLabel;
        fillLevelUnit = getxUnit;
        this.allocationDelay = allocationDelay;
        actuators = new HashMap<Integer, BufferActuator>();
        for (Actuator ac : actuatorCapabilities) {
            actuators.put(ac.getActuatorId(), new BufferActuator(ac));
        }
    }

    public void processSystemDescription(BufferSystemDescription bsd) {
        setLeakageFunction(bsd.getBufferLeakage());

        for (ActuatorBehaviour actuatorDescription : bsd.getActuators()) {
            if (actuators.containsKey(actuatorDescription.getId())) {
                actuators.get(actuatorDescription.getId()).setAllRunningModes(actuatorDescription.getRunningModes());
                // TODO: timerList is not used! Remove timerList eventually.
            } else {
                throw new IllegalArgumentException("The ActuatorId in the BufferSystemDescription is not known.");
            }
        }
    }

    public void processStateUpdate(BufferStateUpdate<Q> bsu)
    {
        currentFillLevel = bsu.getCurrentFillLevel();

        for (ActuatorUpdate actUpdate : bsu.getCurrentRunningMode()) {
            if (actuators.containsKey(actUpdate.getActuatorId())) {
                BufferActuator theActuator = actuators.get(actUpdate.getActuatorId());

                // Update the current RunningMode of this actuator.
                theActuator.setCurrentRunningModeId(actUpdate.getCurrentRunningModeId());

                // Update the timers of the actuators.
                for (TimerUpdate t : actUpdate.getTimerUpdates()) {
                    if (!theActuator.getAllTimers().containsKey(t.getTimerId()))
                    {
                        throw new IllegalArgumentException("The TimerId in the BufferStateUpdate is not known.");
                    }
                    theActuator.updateTimer(t.getTimerId(), t.getFinishedAt());
                }
            } else {
                throw new IllegalArgumentException("The ActuatorId in the BufferStateUpdate is not known.");
            }
        }
    }

    public Map<BufferActuator, List<Measurable<?>>> getElectricalActuatorsWithReachableDemands(Date now,
                                                                                               double fillLevel) {
        Map<BufferActuator, List<Measurable<?>>> result = new HashMap<BufferActuator, List<Measurable<?>>>();
        for (BufferActuator a : actuators.values()) {
            if (a.getCommodities().contains(Commodity.ELECTRICITY)) {
                result.put(a, a.getPossibleDemands(now, fillLevel));
            }
        }
        return result;
    }

    public double getCurrentFillFraction() {
        // TODO: Check that the unit of the current fill level is right.
        return currentFillLevel.doubleValue(fillLevelUnit) / (getMaximumFillLevel() - getMinimumFillLevel());
    }

    private double getMinimumFillLevel() {
        double lowestBound = Double.MAX_VALUE;
        for (BufferActuator a : actuators.values()) {
            for (RunningMode<FillLevelFunction<RunningModeBehaviour>> mode : a.getAllRunningModes().values())
            {
                lowestBound = Math.min(lowestBound, mode.getValue().getLowerBound());
            }
        }
        return lowestBound;
    }

    private double getMaximumFillLevel() {
        double highestBound = Double.MIN_VALUE;
        for (BufferActuator a : actuators.values()) {
            for (RunningMode<FillLevelFunction<RunningModeBehaviour>> mode : a.getAllRunningModes().values())
            {
                highestBound = Math.min(highestBound, mode.getValue().getUpperBound());
            }
        }
        return highestBound;
    }

    // TODO: Give fill level tussen 0 en 1 (temperatuur). (koelkast laag/ koelkast hoog)

    private void setLeakageFunction(FillLevelFunction<LeakageRate> bufferLeakage) {
        leakageFunction = bufferLeakage;
    }

}
