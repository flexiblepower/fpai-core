package org.flexiblepower.api.efi.bufferhelper;

import java.util.ArrayList;
import java.util.Collection;
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
import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.TimerUpdate;
import org.flexiblepower.rai.values.Commodity;

/**
 * This class processes EFI messages: (buffer registration, system description and update messages). It offers helper
 * methods for the agent to make bids.
 *
 * @param <Q>
 *            The quantity that describes what is stored in the buffer (e.g. temperature or electricity).
 */
public class Buffer<Q extends Quantity> {
    private final String resourceId;
    private final String fillLevelLabel;
    private final Unit<Q> fillLevelUnit;
    private final Measurable<Duration> allocationDelay;
    private final Map<Integer, BufferActuator> actuators;
    private FillLevelFunction<LeakageRate> leakageFunction;
    private Measurable<Q> currentFillLevel;
    private boolean hasReceivedSystemDescription = false;
    private boolean hasReceivedStateUpdate = false;

    /**
     * A Buffer may only be constructed from a complete BufferRegistration message.
     *
     * @param br
     *            A complete buffer registration message. (This is enforced in the message constructor).
     */
    public Buffer(BufferRegistration<Q> br) {
        this(br.getResourceId(),
             br.getFillLevelLabel(),
             br.getFillLevelUnit(),
             br.getAllocationDelay(),
             br.getActuators());
        // Null check not necessary because illegal message may not be constructed.
    }

    /**
     * Private constructor for the components of buffer registration messages.
     *
     * @param resourceId
     * @param getxLabel
     * @param getxUnit
     * @param allocationDelay
     * @param actuatorCapabilities
     */
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

    /**
     * BufferSystemDescription message's information is copied to the internal model of the buffer.
     *
     * @param bsd
     *            The buffer system description message.
     */
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
        hasReceivedSystemDescription = true;
    }

    /**
     * BufferStateUpdate information is added to the internal model.
     *
     * @param bsu
     *            The BufferStateUpdate message.
     */
    public void processStateUpdate(BufferStateUpdate<Q> bsu)
    {
        if (!hasReceivedSystemDescription) {
            return;
        }
        currentFillLevel = bsu.getCurrentFillLevel();

        for (ActuatorUpdate actUpdate : bsu.getCurrentRunningMode()) {
            if (actuators.containsKey(actUpdate.getActuatorId())) {
                BufferActuator theActuator = actuators.get(actUpdate.getActuatorId());

                if (!theActuator.hasRunningMode(actUpdate.getCurrentRunningModeId()))
                {
                    throw new IllegalArgumentException("The RunningModeId in this message is not known.");
                }
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
        hasReceivedStateUpdate = true;
    }

    /**
     * Checks all the actuators for those of type electricity and returns them in an ArrayList.
     *
     * @return A list of all electrical BufferActuators or an empty list if none are found.
     */
    public List<BufferActuator> getElectricalActuators() {
        List<BufferActuator> result = new ArrayList<BufferActuator>();
        for (BufferActuator a : actuators.values()) {
            if (a.getSupportedCommodities().contains(Commodity.ELECTRICITY)) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Checks all the actuators for those of type electricity and returns them in an ArrayList.
     *
     * @return A Map of all electrical BufferActuators or an empty list if none are found. The key is the Actuator Id.
     */
    public Map<Integer, BufferActuator> getElectricalActuatorMap() {
        Map<Integer, BufferActuator> result = new HashMap<Integer, BufferActuator>();
        for (BufferActuator a : actuators.values()) {
            if (a.getSupportedCommodities().contains(Commodity.ELECTRICITY)) {
                result.put(a.getActuatorId(), a);
            }
        }
        return result;
    }

    /**
     * Gets the fill level of the buffer relative to the maximum and minimum fill level. It is expressed in the fill
     * level unit that is defined in the registration message.
     *
     * @return The fill fraction computed where 0 is minimum and 1 is the maximum fill level.
     */
    public double getCurrentFillFraction() {
        double minimumFillLevel = getMinimumFillLevel();
        double maximumFillLevel = getMaximumFillLevel();

        if (maximumFillLevel == minimumFillLevel) {
            throw new IllegalArgumentException("Maximum and Minimum Fill Level may not be the same.");
        } else if (maximumFillLevel < minimumFillLevel) {
            throw new IllegalArgumentException("Maximum Fill level may not be below Minimum Fill Level.");
        }

        return (currentFillLevel.doubleValue(fillLevelUnit) - minimumFillLevel) / (maximumFillLevel - minimumFillLevel);
    }

    /**
     * Gets the current fill level of the buffer.
     *
     * @return A Measurable object containing the current fill level of the buffer and quantity information.
     * @throws IllegalStateException
     *             When no state update has been received.
     */
    public Measurable<Q> getCurrentFillLevel() {
        if (!hasReceivedStateUpdate) {
            throw new IllegalStateException("Cannot give a fill level when no state update has been sent yet.");
        }
        return currentFillLevel;
    }

    /**
     * Returns whether this Buffer has received a system description or not yet.
     *
     * @return Whether this buffer has received a system description yet.
     */
    public boolean hasReceivedSystemDescription() {
        return hasReceivedSystemDescription;
    }

    /**
     * Returns whether a state update has been received.
     *
     * @return Whether it has received state update.
     */
    public boolean hasReceivedStateUpdate() {
        return hasReceivedStateUpdate;
    }

    /**
     *
     * @return The minimum of all actuators, not only the electrical.
     */
    public double getMinimumFillLevel() {
        double lowestBound = Double.MAX_VALUE;
        if (!hasReceivedSystemDescription) {
            throw new IllegalStateException("Cannot give a minimum fill level when no system description has been sent yet.");
        }
        for (BufferActuator a : actuators.values()) {
            lowestBound = Math.min(lowestBound, a.getMinimumFillLevel());
        }
        return lowestBound;
    }

    /**
     *
     * @return The maximum of all actuators, not only the electrical.
     */
    public double getMaximumFillLevel() {
        double highestBound = Double.MIN_VALUE;
        if (!hasReceivedSystemDescription) {
            throw new IllegalStateException("Cannot give a maximum fill level when no system description has been sent yet.");
        }
        for (BufferActuator a : actuators.values()) {
            highestBound = Math.max(highestBound, a.getMaximumFillLevel());
        }
        return highestBound;
    }

    /**
     * Sets the buffer leakage function.
     *
     * @param bufferLeakage
     *            The buffer leakage function
     */
    private void setLeakageFunction(FillLevelFunction<LeakageRate> bufferLeakage) {
        leakageFunction = bufferLeakage;
    }

    // TODO: Forecast and target update message interpretation.
    /**
     * Gets the allocation delay.
     *
     * @return A Measurable that represents the allocation delay as a duration.
     */
    public Measurable<Duration> getAllocationDelay() {
        return allocationDelay;
    }

    /**
     * Returns the buffer leakage function.
     *
     * @return The leakage function of the buffer.
     */
    public FillLevelFunction<LeakageRate> getLeakageFunction() {
        return leakageFunction;
    }

    /**
     * Gets the identifier of the resource.
     *
     * @return
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Gets the label of the fill level.
     *
     * @return The label of the fill level.
     */
    public String getFillLevelLabel() {
        return fillLevelLabel;
    }

    /**
     * Gets the default fill level unit of this buffer.
     *
     * @return The default unit in which the fill level is expressed.
     */
    public Unit<Q> getUnit() {
        return fillLevelUnit;
    }

    /**
     * Gets all actuators indexed on their id.
     *
     * @return A map with all actuators indexed on their id.
     */
    public Map<Integer, BufferActuator> getActuators() {
        return actuators;
    }

    /**
     * Returns the Actuator object based on the id.
     *
     * @param id
     *            The id of the actuator.
     * @return The actuator object.
     */
    public BufferActuator getActuatorById(int id) {
        if (actuators.containsKey(id)) {
            return actuators.get(id);
        }
        else {
            return null;
        }
    }
}
