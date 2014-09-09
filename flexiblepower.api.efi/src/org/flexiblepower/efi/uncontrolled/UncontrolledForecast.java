package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.rai.values.CommodityForecast;

/**
 * This message is optional and can only be used if there is a mechanism in the resource manager that can calculate a
 * consumption/production forecast.
 */
public final class UncontrolledForecast extends UncontrolledUpdate {
    private static final long serialVersionUID = -1655075711659394977L;

    private final CommodityForecast forecast;

    public UncontrolledForecast(String resourceId,
                                Date timestamp,
                                Date validFrom,
                                CommodityForecast forecast) {
        super(resourceId, timestamp, validFrom);
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
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((forecast == null) ? 0 : forecast.hashCode());
        return result;
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
        if (forecast == null) {
            if (other.forecast != null) {
                return false;
            }
        } else if (!forecast.equals(other.forecast)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("forecast=").append(forecast).append(", ");
    }
}
