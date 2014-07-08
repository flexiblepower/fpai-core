package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.comm.ResourceHandshake;
import org.flexiblepower.rai.values.Commodity;

public class UncontrolledCapabilities extends ResourceHandshake {

	private final Set<Commodity> supportedCommodities;

	public UncontrolledCapabilities(String resourceId, Date timestamp,
			Set<Commodity> supportedCommodities) {
		super(resourceId, timestamp);
		this.supportedCommodities = supportedCommodities;
	}

	public Set<Commodity> getSupportedCommodities() {
		return supportedCommodities;
	}

}
