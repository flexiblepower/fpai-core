package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.TargetProfile;

public class BufferTargetProfileUpdate extends BufferUpdate {

    private static final long serialVersionUID = 8241650405419768302L;

    private final Date startTime;
    private final TargetProfile targetProfile;

    public BufferTargetProfileUpdate(String resourceId,
                                     Date timestamp,
                                     Date validFrom,
                                     Measurable<Duration> allocationDelay,
                                     Date startTime,
                                     TargetProfile targetProfile) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.startTime = startTime;
        this.targetProfile = targetProfile;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public TargetProfile getTargetProfile() {
        return targetProfile;
    }

}
