package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ResourceUpdate;

public class BufferUpdate extends BufferResourceUpdate {

	private static final long serialVersionUID = 899987142667364534L;

	public static class ActuatorUpdate {
		private final int actuatorId;
		private final int currentRunningModeId;
		private final Set<TimerUpdate> timerUpdates;

		public ActuatorUpdate(int actuatorId, int currentRunningModeId,
				Set<TimerUpdate> timerUpdates) {
			super();
			this.actuatorId = actuatorId;
			this.currentRunningModeId = currentRunningModeId;
			this.timerUpdates = timerUpdates;
		}

		public int getActuatorId() {
			return actuatorId;
		}

		public int getCurrentRunningModeId() {
			return currentRunningModeId;
		}

		public Set<TimerUpdate> getTimerUpdates() {
			return timerUpdates;
		}

	}

	public static class TimerUpdate {
		public int timedId;
		public Date finishedAt;
	}

	private double xValue;
	private Set<ActuatorUpdate> currentRunningMode;

	public BufferUpdate(String resourceId, Date timestamp, Date validFrom,
			Measurable<Duration> allocationDelay, double xValue,
			Set<ActuatorUpdate> currentRunningMode) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.xValue = xValue;
		this.currentRunningMode = currentRunningMode;
	}

	public double getxValue() {
		return xValue;
	}

	public Set<ActuatorUpdate> getCurrentRunningMode() {
		return currentRunningMode;
	}

}
