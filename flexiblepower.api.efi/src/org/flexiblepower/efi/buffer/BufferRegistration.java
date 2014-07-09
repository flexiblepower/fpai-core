package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

public class BufferRegistration extends ControlSpaceRegistration {

	public static class ActuatorCapabilities {
		private int actuatorId;
		private String actuatorLabel;
		private Set<Commodity> commodities;
	}

	public BufferRegistration(String resourceId, Date timestamp,
			Set<ActuatorCapabilities> actuatorCapabilities) {
		super(resourceId, timestamp);
		this.actuatorCapabilities = actuatorCapabilities;
	}

	private Set<ActuatorCapabilities> actuatorCapabilities;

}
