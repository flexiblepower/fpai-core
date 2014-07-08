package org.flexiblepower.efi.timeshifter;

import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.efi.values.CommodityProfile;

public class SequentialProfile {
	private int id;
	private Measurable<Duration> maxIntervalBefore;
	private List<CommodityProfile> commodityProfiles;

	public SequentialProfile(int id, Measurable<Duration> maxIntervalBefore,
			List<CommodityProfile> commodityProfiles) {
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

	public List<CommodityProfile> getCommodityProfiles() {
		return commodityProfiles;
	}

}
