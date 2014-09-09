package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.ControlSpaceUpdate;

/**
 * A marker class for uncontrolled updates. Partent of {@link UncontrolledMeasurement} and {@link UncontrolledForecast}
 * 
 * @author TNO
 * 
 */
public abstract class UncontrolledUpdate extends ControlSpaceUpdate {

    private static final long serialVersionUID = 9154440319073601863L;

    public UncontrolledUpdate(String resourceId, Date timestamp, Date validFrom, Measurable<Duration> allocationDelay) {
        super(resourceId, timestamp, validFrom, allocationDelay);
    }

}
