package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public abstract class BufferUpdate extends ControlSpaceUpdate {

	private static final long serialVersionUID = 1166196948866257737L;

	public BufferUpdate(String resourceId, Date timestamp,
			Date validFrom, Measurable<Duration> allocationDelay) {
		super(resourceId, timestamp, validFrom, allocationDelay);
	}

}
