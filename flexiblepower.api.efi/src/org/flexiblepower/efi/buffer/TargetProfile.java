package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.List;

import org.flexiblepower.rai.comm.ResourceUpdate;

public class TargetProfile extends ResourceUpdate {
	private final Date startTime;
	private final List<TargetProfileElement> targetProfileElements;

	public TargetProfile(String resourceId, Date timestamp, Date startTime,
			List<TargetProfileElement> targetProfileElements) {
		super(resourceId, timestamp);
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
