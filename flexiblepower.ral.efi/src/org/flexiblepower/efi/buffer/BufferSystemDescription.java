package org.flexiblepower.efi.buffer;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.flexiblepower.efi.util.FillLevelFunction;

/**
 * This class is derived from BufferUpdate and contains the actuators and the buffer leakage information.
 */
public class BufferSystemDescription extends BufferUpdate {
    private final Map<Integer, ActuatorBehaviour> actuators;
    private final FillLevelFunction<LeakageRate> bufferLeakage;

    /**
     * Constructs a new {@link BufferSystemDescription} message with the given parameters
     *
     * @param bufferRegistration
     *            The original registration on which this message will be based. This will copy the resourceId from the
     *            {@link BufferRegistration} and will check if the defined actuator behaviour is complete.
     * @param timestamp
     *            The moment when this constructor is called
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     * @param actuators
     *            This is a list of zero or more actuators that affect this buffer.
     * @param bufferLeakage
     *            This attribute contains all the information on the leakage of this buffer.
     */
    public BufferSystemDescription(BufferRegistration<?> bufferRegistration,
                                   Date timestamp,
                                   Date validFrom,
                                   Collection<ActuatorBehaviour> actuators,
                                   FillLevelFunction<LeakageRate> bufferLeakage) {
        super(bufferRegistration.getResourceId(), timestamp, validFrom);
        if (actuators == null) {
            throw new NullPointerException("actuators");
        } else if (bufferLeakage == null) {
            throw new NullPointerException("bufferLeakage");
        }

        double lowerBound = bufferLeakage.getLowerBound();
        double upperBound = bufferLeakage.getUpperBound();

        Map<Integer, ActuatorBehaviour> temp = new TreeMap<Integer, ActuatorBehaviour>();
        for (ActuatorBehaviour actuatorBehaviour : actuators) {
            if (actuatorBehaviour.getLowerBound() < lowerBound || actuatorBehaviour.getUpperBound() > upperBound) {
                throw new IllegalArgumentException("The actuator(" + actuatorBehaviour.getId()
                                                   + ") is working on a bound ("
                                                   + actuatorBehaviour.getLowerBound()
                                                   + " - "
                                                   + actuatorBehaviour.getUpperBound()
                                                   + ") outside of the bound given by the leakageFunction ("
                                                   + lowerBound
                                                   + " - "
                                                   + upperBound
                                                   + ")");
            }

            if (bufferRegistration.getActuator(actuatorBehaviour.getId()) == null) {
                throw new IllegalArgumentException("Describing the behaviour of an actuator with an unknown id (" + actuatorBehaviour.getId()
                                                   + ")");
            } else if (temp.containsKey(actuatorBehaviour.getId())) {
                throw new IllegalArgumentException("The actuator with id (" + actuatorBehaviour.getId()
                                                   + ") is described twice");
            }

            temp.put(actuatorBehaviour.getId(), actuatorBehaviour);
        }

        HashSet<Integer> missingIds = new HashSet<Integer>(bufferRegistration.getActuatorsMap().keySet());
        missingIds.removeAll(temp.keySet());
        if (!missingIds.isEmpty()) {
            throw new IllegalArgumentException("Missing the following actuator descriptions: " + missingIds);
        }

        this.actuators = Collections.unmodifiableMap(temp);
        this.bufferLeakage = bufferLeakage;
    }

    /**
     * @return This is a list of zero or more actuators that affect this buffer.
     */
    public Collection<ActuatorBehaviour> getActuators() {
        return actuators.values();
    }

    /**
     * @return This attribute contains all the information on the leakage of this buffer.
     */
    public FillLevelFunction<LeakageRate> getBufferLeakage() {
        return bufferLeakage;
    }

    /**
     * @return The lower bound of the buffer
     */
    public double getLowerBound() {
        return bufferLeakage.getLowerBound();
    }

    /**
     * @return The upper bound of the buffer
     */
    public double getUpperBound() {
        return bufferLeakage.getUpperBound();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + actuators.hashCode();
        result = prime * result + bufferLeakage.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        BufferSystemDescription other = (BufferSystemDescription) obj;
        if (!actuators.equals(other.actuators)) {
            return false;
        } else if (!bufferLeakage.equals(other.bufferLeakage)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("actuators=").append(actuators.values()).append(", ");
        sb.append("bufferLeakage=").append(bufferLeakage).append(", ");
    }
}
