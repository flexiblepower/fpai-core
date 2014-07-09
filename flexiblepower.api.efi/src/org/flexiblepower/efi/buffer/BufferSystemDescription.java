package org.flexiblepower.efi.buffer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

public class BufferSystemDescription extends BufferUpdate {

	private static final long serialVersionUID = -581627020537487583L;

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
	private final Unit<?> xUnit;

	private RunningMode bufferLeakage;

	public BufferSystemDescription(String resourceId, Date timestamp, Date validFrom,
			Measurable<Duration> allocationDelay, List<Actuator> actuators,
			String xLabel, Unit<?> xUnit, RunningMode bufferLeakage) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.actuators = actuators;
		this.xLabel = xLabel;
		this.xUnit = xUnit;
		this.bufferLeakage = bufferLeakage;
	}

	public List<Actuator> getActuators() {
		return actuators;
	}

	public String getxLabel() {
		return xLabel;
	}

	public Unit<?> getxUnit() {
		return xUnit;
	}

	public RunningMode getBufferLeakage() {
		return bufferLeakage;
	}

}
