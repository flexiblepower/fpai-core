package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceUpdate;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommodityForecast;

@SuppressWarnings("rawtypes")
public class UncontrolledUpdate extends ControlSpaceUpdate {

    private static final long serialVersionUID = 9154440319073601863L;

    private final Map<Commodity, CommodityForecast> profiles;

    public UncontrolledUpdate(String resourceId,
                              Date timestamp,
                              Date validFrom,
                              Measurable<Duration> allocationDelay,
                              Map<Commodity, CommodityForecast> profiles) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.profiles = profiles;
    }

    public Map<Commodity, CommodityForecast> getProfiles() {
        return profiles;
    }

    public CommodityForecast getProfileForCommodity(Commodity commodity) {
        return profiles.get(commodity);
    }

}
