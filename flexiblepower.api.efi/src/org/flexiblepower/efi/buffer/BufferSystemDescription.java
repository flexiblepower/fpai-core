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

    // TODO Is Leakage a running mode? Or should we have a new term for
    // RunningMode...
    private final LeakageProfile bufferLeakage;

    public BufferSystemDescription(String resourceId,
                                   Date timestamp,
                                   Date validFrom,
                                   Measurable<Duration> allocationDelay,
                                   List<Actuator> actuators,
                                   LeakageProfile bufferLeakage) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.actuators = actuators;
        this.bufferLeakage = bufferLeakage;
    }

    public List<Actuator> getActuators() {
        return actuators;
    }

    public LeakageProfile getBufferLeakage() {
        return bufferLeakage;
    }

    public double getMinimumFillLevel() {
        // TODO: implement
        return 0;
    }

    public double getMaximumFillLevel() {
        // TODO: implement
        return 0;
    }

}
