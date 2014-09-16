package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.quantity.Quantity;

import org.flexiblepower.rai.values.UncertainMeasurableProfile;

public class BufferUsageForecast<Q extends Quantity> extends BufferUpdate {
    private final UncertainMeasurableProfile<Q> profile;

    public BufferUsageForecast(BufferRegistration<Q> bufferRegistration,
                               Date timestamp,
                               Date validFrom,
                               UncertainMeasurableProfile<Q> profile) {
        super(bufferRegistration.getResourceId(), timestamp, validFrom);
        this.profile = profile;
    }

    public UncertainMeasurableProfile<Q> getProfile() {
        return profile;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + profile.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        BufferUsageForecast other = (BufferUsageForecast) obj;
        return other.profile.equals(profile);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("profile=").append(profile).append(", ");
    }
}
