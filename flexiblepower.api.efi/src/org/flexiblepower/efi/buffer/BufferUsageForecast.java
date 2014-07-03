package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ResourceInfo;

public class BufferUsageForecast extends ResourceInfo {

	public BufferUsageForecast(String resourceId, Date timestamp) {
		super(resourceId, timestamp);
	}

	private final Date startTime = null;
	private final Element[] profile = null;

	public static class Element {
		private final Measurable<Duration> duration = null;
		private final Double xs = null;
	}

}
