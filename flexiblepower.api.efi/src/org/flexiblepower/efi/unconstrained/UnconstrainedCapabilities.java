package org.flexiblepower.efi.unconstrained;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;

import org.flexiblepower.rai.comm.ResourceHandshake;
import org.flexiblepower.rai.values.Commodity;

public class UnconstrainedCapabilities extends ResourceHandshake {

	private final Set<Commodity> supportedCommodities;

	public UnconstrainedCapabilities(String resourceId, Date timestamp,
			Set<Commodity> supportedCommodities) {
		super(resourceId, timestamp);
		this.supportedCommodities = supportedCommodities;
	}

	public Set<Commodity> getSupportedCommodities() {
		return supportedCommodities;
	}

}
