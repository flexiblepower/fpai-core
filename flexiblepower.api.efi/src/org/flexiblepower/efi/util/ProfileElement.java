package org.flexiblepower.efi.util;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public interface ProfileElement<PE extends ProfileElement<PE>> {

	public Measurable<Duration> getDuration();

	public PE subProfile(Measurable<Duration> offset,
			Measurable<Duration> duration);

}
