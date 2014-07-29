package org.flexiblepower.efi.buffer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class BufferSystemDescription extends BufferUpdate {

    private static final long serialVersionUID = -581627020537487583L;

    /**
     * The list of actuator that can affect the range of the buffer
     */
    private List<Actuator> actuators = new ArrayList<Actuator>();

    private final LeakageFunction bufferLeakage;

    public BufferSystemDescription(String resourceId,
                                   Date timestamp,
                                   Date validFrom,
                                   Measurable<Duration> allocationDelay,
                                   List<Actuator> actuators,
                                   LeakageFunction bufferLeakage) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.actuators = actuators;
        this.bufferLeakage = bufferLeakage;
    }

    /**
     * Lists all the actuators that can influence the buffer
     * 
     * @return List of all the actuators that can influence the buffer
     */
    public List<Actuator> getActuators() {
        return actuators;
    }

    /**
     * Returns the leakage function of the buffer.
     * 
     * @return The buffer leakage function
     */
    public LeakageFunction getBufferLeakage() {
        return bufferLeakage;
    }

    /**
     * Determines the minimum fill level for which there is an actuator that can operate.
     * 
     * @return The minimum fill level for which there is an actuator that can operate
     */
    public double getMinimumFillLevel() {
        double min = Double.MAX_VALUE;
        for (Actuator a : actuators) {
            double minFillLevel = a.minFillLevel();
            if (min > minFillLevel) {
                min = minFillLevel;
            }
        }
        return min;
    }

    /**
     * Determines the maximum fill level for which there is an actuator that can operate.
     * 
     * @return The maximum fill level for which there is an actuator that can operate
     */
    public double getMaximumFillLevel() {
        double max = Double.MIN_VALUE;
        for (Actuator a : actuators) {
            double maxFillLevel = a.maxFillLevel();
            if (max < maxFillLevel) {
                max = maxFillLevel;
            }
        }
        return max;
    }

}
