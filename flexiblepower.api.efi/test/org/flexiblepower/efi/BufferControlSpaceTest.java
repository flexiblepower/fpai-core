package org.flexiblepower.efi;

import junit.framework.TestCase;

public class BufferControlSpaceTest extends TestCase {

	public void testValidationTests() {
//		create(ConstraintList.create(SI.WATT).addSingle(0).build());
//		create(ConstraintList.create(SI.WATT).addSingle(0).addSingle(POS_NR)
//				.build());
//		create(ConstraintList.create(SI.WATT).addSingle(0).addSingle(NEG_NR)
//				.build());
//		create(ConstraintList.create(SI.WATT).addSingle(POS_NR).build());
//		create(ConstraintList.create(SI.WATT).addSingle(NEG_NR).build());
//		create(ConstraintList.create(SI.WATT).addRange(0, POS_NR).build());
//		create(ConstraintList.create(SI.WATT).addRange(NEG_NR, 0).build());
//
//		createError(ConstraintList.create(SI.WATT).addSingle(NEG_NR)
//				.addSingle(POS_NR).build());
//		createError(ConstraintList.create(SI.WATT).addRange(NEG_NR, POS_NR)
//				.build());
	}

//	private void create(ConstraintList<Power> chargeSpeed) {
//		Date now = new Date();
//		Date future = TimeUtil.add(now, Measure.valueOf(1, NonSI.HOUR));
//		Measurable<Energy> capacity = Measure.valueOf(1, NonSI.KWH);
//		Measurable<Power> selfDischarge = Measure.valueOf(1, SI.WATT);
//		Measurable<Duration> minute = Measure.valueOf(1, NonSI.MINUTE);
//
//		// First we try to create a couple of valid ones
//		new BufferControlSpace("xxx", now, future, future, capacity, 0,
//				chargeSpeed, selfDischarge, minute, minute, future, 1.);
//	}
//
//	private void createError(ConstraintList<Power> chargeSpeed) {
//		try {
//			create(chargeSpeed);
//			fail("Expected failure for arguments: " + chargeSpeed);
//		} catch (IllegalArgumentException ex) {
//			// OK
//		}
//	}
}
