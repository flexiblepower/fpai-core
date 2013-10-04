package org.flexiblepower.rai.values;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.flexiblepower.rai.unit.EnergyUnit;
import org.flexiblepower.rai.unit.PowerUnit;
import org.flexiblepower.rai.unit.TimeUnit;
import org.flexiblepower.rai.values.EnergyProfile.Element;

/**
 * EnergyProfile representing a series of EnergyOverDuration items, where the durations are not necessarily equal. It is
 * considered as a set of consecutive periods for which one has Energy measurements over each period.
 * 
 * PMSuite - PM Data Specification - v0.6
 */
public class EnergyProfile extends AbstractList<Element> {
    public static class Element {
        private final Duration duration;
        private final EnergyValue energy;

        public Element(Duration duration, EnergyValue energy) {
            if (duration == null || energy == null) {
                throw new NullPointerException();
            }
            this.duration = duration;
            this.energy = energy;
        }

        public Duration getDuration() {
            return duration;
        }

        public EnergyValue getEnergy() {
            return energy;
        }

        public PowerValue getAveragePower() {
            double joules = energy.getValueAs(EnergyUnit.JOULE);
            double seconds = duration.getValueAs(TimeUnit.SECONDS);
            return new PowerValue(joules / seconds, PowerUnit.WATT);
        }

        @Override
        public String toString() {
            return "(" + duration + "," + energy + ")";
        }
    }

    public static class Builder {
        private Duration duration;
        private final List<Element> profile;

        public Builder() {
            duration = null;
            profile = new ArrayList<Element>();
        }

        public Builder add(Duration duration, EnergyValue energy) {
            profile.add(new Element(duration, energy));
            return this;
        }

        public Builder add(Duration duration, double value, EnergyUnit unit) {
            profile.add(new Element(duration, new EnergyValue(value, unit)));
            return this;
        }

        public Builder add(EnergyValue energy) {
            profile.add(new Element(duration, energy));
            return this;
        }

        public Builder add(double value, EnergyUnit unit) {
            profile.add(new Element(duration, new EnergyValue(value, unit)));
            return this;
        }

        public Builder setDuration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public EnergyProfile build() {
            return new EnergyProfile(profile.toArray(new Element[profile.size()]));
        }
    }

    private final Element[] profile;

    public EnergyProfile(Duration duration, EnergyUnit unit, double... values) {
        profile = new Element[values.length];
        for (int ix = 0; ix < values.length; ix++) {
            profile[ix] = new Element(duration, new EnergyValue(values[ix], unit));
        }
    }

    public EnergyProfile(Duration duration, EnergyValue... measurements) {
        profile = new Element[measurements.length];
        for (int ix = 0; ix < measurements.length; ix++) {
            profile[ix] = new Element(duration, measurements[ix]);
        }
    }

    EnergyProfile(Element[] profile) {
        this.profile = profile;
    }

    public Element[] getProfile() {
        return Arrays.copyOf(profile, profile.length);
    }

    public EnergyValue getValueForOffset(Duration offset) {
        Element e = getElementForOffset(offset);
        return e == null ? null : e.getEnergy();
    }

    public Element getElementForOffset(Duration offset) {
        double offsetDone = 0;
        double offsetNeeded = offset.getValue();
        TimeUnit timeUnit = offset.getUnit();

        for (Element element : profile) {
            offsetDone += element.duration.getValueAs(timeUnit);
            if (offsetDone >= offsetNeeded) {
                return element;
            }
        }

        return null;
    }

    public EnergyValue getTotalEnergy(EnergyUnit unit) {
        double value = 0;
        for (Element part : profile) {
            value += part.getEnergy().getValueAs(unit);
        }
        return new EnergyValue(value, unit);
    }

    public Duration getDuration() {
        double duration = 0;

        for (Element e : getProfile()) {
            duration += e.getDuration().getValueAs(TimeUnit.MILLISECONDS);
        }

        return new Duration(duration, TimeUnit.MILLISECONDS);
    }

    /**
     * @param offset
     *            The offset in relationship to the start of this profile (may be negative, in that case, the 'sub'
     *            profile will be prepended with 0W)
     * @param duration
     *            The duration of the profile (if offset+duration is longer than this profile, the 'sub' profile will
     *            appended with 0W)
     * @return A portion of this profile defined by the offset and the duration of this profile to include.
     */
    public EnergyProfile subprofile(Duration offset, Duration duration) {
        // duration must be specified
        if (duration == null) {
            throw new IllegalArgumentException("No duration provided for the subprofile");
        }

        // if the duration is zero, return an empty profile
        if (duration.getValue() == 0) {
            return new EnergyProfile(duration);
        }

        // if the original profile is empty, return an empty profile
        if (getDuration().getValue() == 0) {
            return new EnergyProfile(duration);
        }

        // if there is no offset use 0
        if (offset == null) {
            offset = Duration.ZERO;
        }

        // if the offset is larger than the profile, return an empty profile
        if (offset.compareTo(getDuration()) > 0) {
            return new EnergyProfile(duration);
        }

        EnergyProfile.Builder builder = new EnergyProfile.Builder();

        // the unit to use throughout the calculations
        TimeUnit timeUnit = offset.getUnit();

        // the start, duration and end of the sub profile
        double subprofileStart = offset.getValue();
        double subprofileDuration = duration.getValueAs(timeUnit);
        double subprofileEnd = subprofileStart + subprofileDuration;

        // variables for the start, duration and end of a profile element
        double eStart = 0, eDuration, eEnd;

        // prepend if offset is negative
        if (subprofileStart < 0) {
            builder.add(new Duration(subprofileStart * -1, timeUnit), 0, EnergyUnit.JOULE);
        }

        for (int i = 0; i < size(); i++) {
            Element e = get(i);
            eDuration = e.getDuration().getValueAs(timeUnit);
            eEnd = eStart + eDuration;

            // reached the end of the sub profile
            if (eStart > subprofileEnd) {
                break;
            }

            // this element should end up in the sub profile (or at least part of it)
            if (eEnd > subprofileStart) {
                // calculate how much time to skip at the start and/or end of the element
                double timeToSkipBeforeStart = subprofileStart - eStart;
                timeToSkipBeforeStart = timeToSkipBeforeStart < 0 ? 0 : timeToSkipBeforeStart;
                double timeToSkipAfterEnd = eEnd - subprofileEnd;
                timeToSkipAfterEnd = timeToSkipAfterEnd < 0 ? 0 : timeToSkipAfterEnd;
                double timeToSkip = timeToSkipBeforeStart + timeToSkipAfterEnd;

                // add the full element
                if (timeToSkip == 0) {
                    builder.add(e.getDuration(), e.getEnergy());
                }

                // or add part of the element
                else {
                    double timeToKeep = eDuration - timeToSkip;
                    double energyValue = e.getEnergy().getValue() * (timeToKeep / eDuration);
                    builder.add(new Duration(timeToKeep, timeUnit), energyValue, e.getEnergy().getUnit());
                }
            }

            eStart = eEnd;
        }

        // append empty element if duration is longer than profile
        double remainder = subprofileDuration - builder.build().getDuration().getValueAs(timeUnit);
        if (remainder > 0) {
            builder.add(new Duration(remainder, timeUnit), 0, EnergyUnit.JOULE);
        }

        // return the new profile
        return builder.build();
    }

    @Override
    public int hashCode() {
        return 67 * Arrays.hashCode(profile);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            EnergyProfile other = (EnergyProfile) obj;
            return Arrays.equals(profile, other.profile);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(profile);
    }

    @Override
    public int size() {
        return profile.length;
    }

    @Override
    public Element get(int index) {
        return profile[index];
    }
}
