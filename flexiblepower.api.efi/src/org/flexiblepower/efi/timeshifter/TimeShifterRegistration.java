package org.flexiblepower.efi.timeshifter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

/**
 * To register an time shifter appliance driver to an energy service the time shifter registration class is used.
 *
 * @author TNO
 */

public class TimeShifterRegistration extends ControlSpaceRegistration {

    private static final long serialVersionUID = 2453887214286161182L;

    /**
     * The set of all commodities that can be produced or consumed by the appliance.
     */
    private final Set<Commodity<?, ?>> supportedCommodities;

    public TimeShifterRegistration(String resourceId,
                                   Date timestamp,
                                   Measurable<Duration> allocationDelay,
                                   Collection<Commodity<?, ?>> supportedCommodities) {
        super(resourceId, timestamp, allocationDelay);
        this.supportedCommodities = Collections.unmodifiableSet(new HashSet<Commodity<?, ?>>(supportedCommodities));
    }

    public TimeShifterRegistration(String resourceId,
                                   Date timestamp,
                                   Measurable<Duration> allocationDelay,
                                   Commodity<?, ?>... supportedCommodities) {
        this(resourceId, timestamp, allocationDelay, Arrays.asList(supportedCommodities));
    }

    public Set<Commodity<?, ?>> getSupportedCommodities() {
        return supportedCommodities;
    }
}
