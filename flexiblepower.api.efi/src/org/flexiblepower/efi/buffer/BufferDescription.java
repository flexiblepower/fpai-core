package org.flexiblepower.efi.buffer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ResourceUpdate;

public class BufferDescription extends ResourceUpdate {

	/**
	 * The list of actuator that can affect the range of the buffer
	 */
	private List<Actuator> actuators = new ArrayList<Actuator>();

	/**
	 * The label of x for display purposes in the UI
	 */
	private final String xLabel;

	/**
	 * The unit of x for display purposes in the UI
	 */
	private final String xUnit;

	private RunningMode bufferLeakage;

	public BufferDescription(String resourceId, Date timestamp, Date validFrom,
			Measurable<Duration> allocationDelay, List<Actuator> actuators,
			String xLabel, String xUnit, RunningMode bufferLeakage) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.actuators = actuators;
		this.xLabel = xLabel;
		this.xUnit = xUnit;
		this.bufferLeakage = bufferLeakage;
	}

}
