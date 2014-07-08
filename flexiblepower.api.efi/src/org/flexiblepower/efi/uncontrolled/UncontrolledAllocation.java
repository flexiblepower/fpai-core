package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ResourceUpdate;

public class UncontrolledAllocation extends Allocation {

	private static final long serialVersionUID = -6113496967677840815L;

	public UncontrolledAllocation(String resourceId,
			ResourceUpdate resourceUpdate, Date timestamp) {
		super(resourceId, resourceUpdate, timestamp);
		
		// TODO
	}

}
