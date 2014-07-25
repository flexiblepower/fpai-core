package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.ForecastProfile;

public class BufferUsageForecast extends BufferUpdate {

    private final Date startTime;
    private final ForecastProfile profile;

    public BufferUsageForecast(String resourceId,
                               Date timestamp,
                               Date validFrom,
                               Measurable<Duration> allocationDelay,
                               Date startTime,
                               ForecastProfile profile) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.startTime = startTime;
        this.profile = profile;
    }
}
