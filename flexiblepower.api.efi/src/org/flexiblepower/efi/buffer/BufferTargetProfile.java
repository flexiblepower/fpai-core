package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class BufferTargetProfile extends BufferUpdate {

	private static final long serialVersionUID = 8241650405419768302L;

	private final Date startTime;
	private final List<TargetProfileElement> targetProfileElements;

	public BufferTargetProfile(String resourceId, Date timestamp, Date validFrom,
			Measurable<Duration> allocationDelay, Date startTime,
			List<TargetProfileElement> targetProfileElements) {
		super(resourceId, timestamp, validFrom, allocationDelay);
		this.startTime = startTime;
		this.targetProfileElements = targetProfileElements;
	}

	public Date getStartTime() {
		return startTime;
	}

	public List<TargetProfileElement> getTargetProfileElements() {
		return targetProfileElements;
	}
}
