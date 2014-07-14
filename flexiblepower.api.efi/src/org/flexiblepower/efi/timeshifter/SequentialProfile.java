package org.flexiblepower.efi.timeshifter;

import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.efi.values.CommodityProfile;
import org.flexiblepower.rai.values.Commodity;

public class SequentialProfile {
	private int id;
	private Measurable<Duration> maxIntervalBefore;
	private Map<Commodity, CommodityProfile> commodityProfiles;

	public SequentialProfile(int id, Measurable<Duration> maxIntervalBefore,
			Map<Commodity, CommodityProfile> commodityProfiles) {
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

	public Map<Commodity, CommodityProfile> getCommodityProfiles() {
		return commodityProfiles;
	}

}
