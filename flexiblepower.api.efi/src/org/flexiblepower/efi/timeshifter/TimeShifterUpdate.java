package org.flexiblepower.efi.timeshifter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.flexiblepower.rai.ControlSpaceUpdate;
import org.flexiblepower.time.TimeService;

/**
 * If a new program is scheduled by time shifter appliance the resource manager sends a time shifter control space
 * update towards the energy app.
 */
public class TimeShifterUpdate extends ControlSpaceUpdate {
    private final Date endBefore;
    private final List<SequentialProfile> timeshifterProfiles;

    /**
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     * @param endBefore
     *            The moment in time where every sequential profile in the timeShifterProfiles list must be finished.
     * @param timeshifterProfiles
     *            A list of {@link SequentialProfile}s representing one or more energy profiles, in a CommodityForecast
     *            object, that can be allocated sequentially. The forecast profile is used because there can be an
     *            uncertainty in the declared profiles. (e.g. the program a tumble dryer controlled by a sensor does not
     *            have a fixed duration and therefore some uncertainty)
     */
    public TimeShifterUpdate(String resourceId,
                             Date timestamp,
                             Date validFrom,
                             Date endBefore,
                             List<SequentialProfile> timeshifterProfiles) {
        super(resourceId, timestamp, validFrom);
        if (endBefore == null) {
            throw new NullPointerException("endBefore");
        }
        if (timeshifterProfiles == null) {
            throw new NullPointerException("timeshifterProfiles");
        }

        this.endBefore = endBefore;
        this.timeshifterProfiles = Collections.unmodifiableList(new ArrayList<SequentialProfile>(timeshifterProfiles));
    }

    /**
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     * @param endBefore
     *            The moment in time where every sequential profile in the timeShifterProfiles list must be finished.
     * @param timeshifterProfiles
     *            A list of {@link SequentialProfile}s representing one or more energy profiles, in a CommodityForecast
     *            object, that can be allocated sequentially. The forecast profile is used because there can be an
     *            uncertainty in the declared profiles. (e.g. the program a tumble dryer controlled by a sensor does not
     *            have a fixed duration and therefore some uncertainty)
     */
    public TimeShifterUpdate(String resourceId,
                             Date timestamp,
                             Date validFrom,
                             Date endBefore,
                             SequentialProfile... timeshifterProfiles) {
        this(resourceId, timestamp, validFrom, endBefore, Arrays.asList(timeshifterProfiles));
    }

    /**
     * @return A list of {@link SequentialProfile}s representing one or more energy profiles, in a CommodityForecast
     *         object, that can be allocated sequentially. The forecast profile is used because there can be an
     *         uncertainty in the declared profiles. (e.g. the program a tumble dryer controlled by a sensor does not
     *         have a fixed duration and therefore some uncertainty)
     */
    public List<SequentialProfile> getTimeShifterProfiles() {
        return timeshifterProfiles;
    }

    /**
     * @return The moment in time where every sequential profile in the timeShifterProfiles list must be finished.
     */
    public Date getEndBefore() {
        return endBefore;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + endBefore.hashCode() * 67 + timeshifterProfiles.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        TimeShifterUpdate other = (TimeShifterUpdate) obj;
        return other.endBefore.equals(endBefore) && other.timeshifterProfiles.equals(timeshifterProfiles);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("endBefore=").append(endBefore).append(", ");
        sb.append("timeshifterProfiles=").append(timeshifterProfiles).append(", ");
    }
}
