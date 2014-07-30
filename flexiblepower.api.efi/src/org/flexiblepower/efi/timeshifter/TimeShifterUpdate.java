package org.flexiblepower.efi.timeshifter;

import java.util.Date;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceUpdate;

/**
 * If a new program is scheduled by time shifter appliance the resource manager sends a time shifter control space
 * update towards the energy app.
 * 
 * @author TNO
 * 
 */
public class TimeShifterUpdate extends ControlSpaceUpdate {

    /**
     * The moment in time where every sequential profile in the timeShifterProfiles list must be finished.
     */
    private final Date endBefore;

    /**
     * A list of SequentialProfile representing one or more energy profiles, in a CommodityForecast object, that can be
     * allocated sequentially. The forecast profile is used because there can be an uncertainty in the declared
     * profiles. (e.g. the program a tumble dryer controlled by a sensor does not have a fixed duration and therefore
     * some uncertainty)
     */
    private final List<SequentialProfile> timeshifterProfiles;

    public TimeShifterUpdate(String resourceId,
                             Date timestamp,
                             Date validFrom,
                             Measurable<Duration> allocationDelay,
                             Date endBefore,
                             List<SequentialProfile> timeshifterProfiles) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.endBefore = endBefore;
        this.timeshifterProfiles = timeshifterProfiles;
    }

    public List<SequentialProfile> getTimeShifterProfiles() {
        return timeshifterProfiles;

    }

}
