package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

public class BufferRegistration extends ControlSpaceRegistration {

	/**
	 * The label of x for display purposes in the UI
	 */
	private final String xLabel;

	/**
	 * The unit of x for display purposes in the UI
	 */
	private final Unit<?> xUnit;

	/**
	 * The duration of the delay in communications channel from the moment of
	 * sending to the moment the command is executed up by the device.
	 */
	private Measurable<Duration> allocationDelay;

	public static class ActuatorCapabilities {
		private int actuatorId;
		private String actuatorLabel;
		private Set<Commodity> commodities;
	}

	private final Set<ActuatorCapabilities> actuatorCapabilities;

	public BufferRegistration(String resourceId, Date timestamp, String xLabel,
			Unit<?> xUnit, Set<ActuatorCapabilities> actuatorCapabilities) {
		super(resourceId, timestamp);
		this.xLabel = xLabel;
		this.xUnit = xUnit;
		this.actuatorCapabilities = actuatorCapabilities;
	}
}
