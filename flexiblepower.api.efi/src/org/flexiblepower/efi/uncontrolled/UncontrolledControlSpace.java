package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.efi.values.CommodityProfile;
import org.flexiblepower.rai.comm.ResourceUpdate;
import org.flexiblepower.rai.values.Commodity;

@SuppressWarnings("rawtypes")
public class UncontrolledControlSpace extends ResourceUpdate {

	private static final long serialVersionUID = 9154440319073601863L;

	private final Map<Commodity, CommodityProfile> profiles;

	public UncontrolledControlSpace(String resourceId, Date timestamp,
			Date validFrom, Measurable<Duration> allocationDelay,
			Map<Commodity, CommodityProfile> profiles) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.profiles = profiles;
	}

	public Map<Commodity, CommodityProfile> getProfiles() {
		return profiles;
	}

	public CommodityProfile getProfileForCommodity(Commodity commodity) {
		return profiles.get(commodity);
	}

}
