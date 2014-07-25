package org.flexiblepower.efi.timeshifter;

import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommodityForecast;

public class SequentialProfile {
    private final int id;
    private final Measurable<Duration> maxIntervalBefore;
    private final Map<Commodity, CommodityForecast> commodityProfiles;

    public SequentialProfile(int id,
                             Measurable<Duration> maxIntervalBefore,
                             Map<Commodity, CommodityForecast> commodityProfiles) {
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

    public Map<Commodity, CommodityForecast> getCommodityProfiles() {
        return commodityProfiles;
    }

}
