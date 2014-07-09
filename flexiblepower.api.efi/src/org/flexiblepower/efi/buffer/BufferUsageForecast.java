package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public class BufferUsageForecast extends BufferUpdate {

	// TODO Hier ben ik nog niet happy mee...

	private final Date startTime;
	private final Element[] profile;

	public BufferUsageForecast(String resourceId, Date timestamp,
			Date validFrom, Measurable<Duration> allocationDelay,
			Date startTime, Element[] profile) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.startTime = startTime;
		this.profile = profile;
	}

	public static class Element {
		private final Measurable<Duration> duration = null;
		private final double xsMean;
		private final double xsStandardDeviation;

		public Element(double xsMean, double xsStandardDeviation) {
			super();
			this.xsMean = xsMean;
			this.xsStandardDeviation = xsStandardDeviation;
		}

	}

}
