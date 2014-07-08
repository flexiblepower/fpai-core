package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ResourceUpdate;

public abstract class BufferResourceUpdate extends ResourceUpdate {

	private static final long serialVersionUID = 1166196948866257737L;

	public BufferResourceUpdate(String resourceId, Date timestamp,
			Date validFrom, Measurable<Duration> allocationDelay) {
		super(resourceId, timestamp, validFrom, allocationDelay);
	}

}
