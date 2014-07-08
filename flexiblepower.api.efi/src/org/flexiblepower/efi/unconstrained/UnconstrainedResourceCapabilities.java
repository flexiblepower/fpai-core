package org.flexiblepower.efi.unconstrained;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;

import org.flexiblepower.rai.comm.ResourceHandshake;

public class UnconstrainedResourceCapabilities extends ResourceHandshake {

	// Commodity consumed or produced
	private Set<Measurable<?>> commodities;

	public UnconstrainedResourceCapabilities(String resourceId, Date timestamp,
			Set<Measurable<?>> commodities) {
		super(resourceId, timestamp);
		this.commodities = commodities;
	}

	public Set<Measurable<?>> getCommodities() {
		return commodities;
	}

}
