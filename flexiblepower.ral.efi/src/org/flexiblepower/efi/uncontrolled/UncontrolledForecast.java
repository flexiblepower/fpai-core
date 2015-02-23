package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.ral.values.CommodityForecast;

/**
 * This message is optional and can only be used if there is a mechanism in the resource manager that can calculate a
 * consumption/production forecast.
 */
public final class UncontrolledForecast extends UncontrolledUpdate {
    private final CommodityForecast forecast;

    public UncontrolledForecast(String resourceId,
                                Date timestamp,
                                Date validFrom,
                                CommodityForecast forecast) {
        super(resourceId, timestamp, validFrom);
        if (forecast == null) {
            throw new NullPointerException("forecast");
        }

        this.forecast = forecast;
    }

    /**
     * @return a {@link CommodityForecast} to represent the estimation how the future usage of commodities will be.
     */
    public CommodityForecast getForecast() {
        return forecast;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + forecast.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UncontrolledForecast other = (UncontrolledForecast) obj;
        return other.forecast.equals(forecast);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("forecast=").append(forecast).append(", ");
    }
}
