package org.flexiblepower.efi.buffer;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.flexiblepower.ral.messages.ControlSpaceRegistration;

/**
 * This class is derived from ControlSpaceRegistration and contains the registration items that are unique to a buffer.
 */
public class BufferRegistration<Q extends Quantity> extends ControlSpaceRegistration {
    private final Map<Integer, Actuator> actuators;
    private final String fillLevelLabel;
    private final Unit<Q> fillLevelUnit;

    /**
     * Constructs the {@link BufferRegistration} object with the given parameters.
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()})
     * @param allocationDelay
     *            The duration of the delay in communications channel from the moment of sending to the moment the
     *            command is executed up by the device.
     * @param fillLevelLabel
     *            A label in human readable format that is being used to refer to the fill level. E.g. tap water
     *            temperature.
     * @param fillLevelUnit
     *            The unit that is applicable to this buffer. E.g. degrees Celsius.
     * @param actuators
     *            This attribute is being used to express the capabilities of all the actuators this buffer appliance
     *            has. E.g. a gas burner and a Stirling engine. This may not be <code>null</code> or be empty.
     */
    public BufferRegistration(String resourceId,
                              Date timestamp,
                              Measurable<Duration> allocationDelay,
                              String fillLevelLabel,
                              Unit<Q> fillLevelUnit,
                              Collection<Actuator> actuators) {
        super(resourceId, timestamp, allocationDelay);

        if (fillLevelLabel == null) {
            throw new NullPointerException("fillLevelLabel");
        } else if (fillLevelUnit == null) {
            throw new NullPointerException("fillLevelUnit");
        } else if (actuators == null) {
            throw new NullPointerException("actuatorCapabilities");
        } else if (actuators.isEmpty()) {
            throw new IllegalArgumentException("actuatorCapabilities is empty");
        }

        this.fillLevelLabel = fillLevelLabel;
        this.fillLevelUnit = fillLevelUnit;

        Map<Integer, Actuator> tempActuators = new TreeMap<Integer, Actuator>();
        for (Actuator actuator : actuators) {
            if (tempActuators.containsKey(actuator.getActuatorId())) {
                throw new IllegalArgumentException("There are multiple actuators with the same identifier (" + actuator.getActuatorId()
                                                   + ")");
            }
            tempActuators.put(actuator.getActuatorId(), actuator);
        }
        this.actuators = Collections.unmodifiableMap(tempActuators);
    }

    /**
     * @return This attribute is being used to express the capabilities of all the actuators this buffer appliance has.
     *         E.g. a gas burner and a Stirling engine.
     */
    public Collection<Actuator> getActuators() {
        return actuators.values();
    }

    /**
     * @param id
     *            The identifier of the actuator
     * @return The actuator with the specified identifier or <code>null</code> if it is not available
     */
    public Actuator getActuator(int id) {
        return actuators.get(id);
    }

    Map<Integer, Actuator> getActuatorsMap() {
        return actuators;
    }

    /**
     * @return A label in human readable format that is being used to refer to the fill level. E.g. tap water
     *         temperature.
     */
    public String getFillLevelLabel() {
        return fillLevelLabel;
    }

    /**
     * @return The unit that is applicable to this buffer. E.g. degrees Celsius.
     */
    public Unit<Q> getFillLevelUnit() {
        return fillLevelUnit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + actuators.hashCode();
        result = prime * result + fillLevelLabel.hashCode();
        result = prime * result + fillLevelUnit.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        BufferRegistration other = (BufferRegistration) obj;
        if (!actuators.equals(other.actuators)) {
            return false;
        } else if (!fillLevelLabel.equals(other.fillLevelLabel)) {
            return false;
        } else if (!fillLevelUnit.equals(other.fillLevelUnit)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("actuators=").append(actuators.values()).append(", ");
        sb.append("fillLevelLabel=").append(fillLevelLabel).append(", ");
        sb.append("fillLevelUnit=").append(fillLevelUnit).append(", ");
    }
}
