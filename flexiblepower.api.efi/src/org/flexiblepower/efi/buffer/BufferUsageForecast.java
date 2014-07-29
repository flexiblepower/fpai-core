package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.CommodityForecast;

public class BufferUsageForecast extends BufferUpdate {

    private static final long serialVersionUID = -5009755748668085977L;

    private final Date startTime;
    private final CommodityForecast<?, ?> profile;

    public BufferUsageForecast(String resourceId,
                               Date timestamp,
                               Date validFrom,
                               Measurable<Duration> allocationDelay,
                               Date startTime,
                               CommodityForecast<?, ?> profile) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.startTime = startTime;
        this.profile = profile;
    }

    public Date getStartTime() {
        return startTime;
    }

    public CommodityForecast<?, ?> getProfile() {
        return profile;
    }

}
