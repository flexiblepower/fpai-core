package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.comm.ResourceCapabilities;
import org.flexiblepower.rai.values.Commodity;

public class BufferCapabilities extends ResourceCapabilities {

	public static class ActuatorCapabilities {
		private String actuatorId;
		private String actuatorLabel;
		private Set<Commodity> commodities;
	}

	public BufferCapabilities(String resourceId, Date timestamp,
			long capabilitiesMessageNr,
			Set<ActuatorCapabilities> actuatorCapabilities) {
		super(resourceId, timestamp);
		this.capabilitiesMessageNr = capabilitiesMessageNr;
		this.actuatorCapabilities = actuatorCapabilities;
	}

	private long capabilitiesMessageNr;
	private Set<ActuatorCapabilities> actuatorCapabilities;

}
