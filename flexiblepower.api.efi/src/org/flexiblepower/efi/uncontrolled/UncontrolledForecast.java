package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommodityForecast;

/**
 * This message is optional and can only be used if there is a mechanism in the resource manager that can calculate a
 * consumption/production forecast.
 * 
 * @author TNO
 * 
 */
public final class UncontrolledForecast extends UncontrolledUpdate {

    private static final long serialVersionUID = -1655075711659394977L;

    /**
     * A map with the as key the Commodity which describes the commodity of the CommodityForecast in key.
     */
    private final Map<Commodity<?, ?>, CommodityForecast<?, ?>> profiles;

    public UncontrolledForecast(String resourceId,
                                Date timestamp,
                                Date validFrom,
                                Measurable<Duration> allocationDelay,
                                Map<Commodity<?, ?>, CommodityForecast<?, ?>> profiles) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.profiles = profiles;
    }

    /**
     * 
     * @return a map of {@link CommodityForecast} for every applicable {@link Commodity}
     */
    public Map<Commodity<?, ?>, CommodityForecast<?, ?>> getProfiles() {
        return profiles;
    }

    /**
     * 
     * @param commodity
     *            the requested {@link Commodity}
     * @return the forecast of the requested commodity, if there is no forecast for the requested commodity this method
     *         returns null
     */

    public CommodityForecast<?, ?> getProfileForCommodity(Commodity<?, ?> commodity) {
        return profiles.get(commodity);
    }

}
