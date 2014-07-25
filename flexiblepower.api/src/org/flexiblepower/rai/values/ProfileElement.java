package org.flexiblepower.rai.values;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public interface ProfileElement<PE extends ProfileElement<PE>> {

    Measurable<Duration> getDuration();

    PE subProfile(Measurable<Duration> offset, Measurable<Duration> duration);

}
