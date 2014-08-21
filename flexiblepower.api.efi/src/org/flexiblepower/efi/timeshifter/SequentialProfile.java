package org.flexiblepower.efi.timeshifter;

import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommodityForecast;

/**
 * A SequentialProfile is typically used by a time shifter appliance driver to indicate the indicate the order of
 * multiple commodity profiles. (e.g. a wahing/drying machine where program “Cotton 40 + Tumble dry” can be scheduled
 * and both parts of the program can be allocated individually but sequentially)
 */
public class SequentialProfile {

    /**
     * Unique id to identify the sequential profile.
     */
    private final int id;

    /**
     * The maximum time between the end of the last SequentialProfile and the start of start of this SequentialProfile.
     * See Figure 12. The maxIntervalBefore of the first sequential profile is typically 0. The maximum time between the
     * end of the last SequentialProfile and the start of start of this SequentialProfile. The maxIntervalBefore of the
     * first sequential profile is typically 0.
     */
    private final Measurable<Duration> maxIntervalBefore;

    /**
     * The forecast profile is used because there can be an uncertainty in the declared profiles. (e.g. the program a
     * tumble dryer controlled by a sensor does not have a fixed duration and therefore some uncertainty)
     */
    private final Map<Commodity<?, ?>, CommodityForecast<?, ?>> commodityProfiles;

    public SequentialProfile(int id,
                             Measurable<Duration> maxIntervalBefore,
                             Map<Commodity<?, ?>, CommodityForecast<?, ?>> commodityProfiles) {
        super();
        this.id = id;
        this.maxIntervalBefore = maxIntervalBefore;
        this.commodityProfiles = commodityProfiles;
    }

    public int getId() {
        return id;
    }

    public Measurable<Duration> getMaxIntervalBefore() {
        return maxIntervalBefore;
    }

    public Map<Commodity<?, ?>, CommodityForecast<?, ?>> getCommodityProfiles() {
        return commodityProfiles;
    }

}
