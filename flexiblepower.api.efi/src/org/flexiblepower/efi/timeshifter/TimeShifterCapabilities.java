package org.flexiblepower.efi.timeshifter;

import java.util.Date;

import org.flexiblepower.rai.comm.ResourceHandshake;

public class TimeShifterCapabilities extends ResourceHandshake {

	private static final long serialVersionUID = 2453887214286161182L;

	public TimeShifterCapabilities(String resourceId, Date timestamp) {
		super(resourceId, timestamp);
	}

}
