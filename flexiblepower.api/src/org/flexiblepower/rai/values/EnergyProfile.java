package org.flexiblepower.rai.values;

import static javax.measure.unit.SI.JOULE;
import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;
import static javax.measure.unit.SI.WATT;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.unit.Unit;

import org.flexiblepower.rai.values.EnergyProfile.Element;

/**
 * EnergyProfile represents a list of duration/energy tuples (see {@link Element}) that represent an energy profile over
 * time, without an explicit starting time.
 */
public class EnergyProfile extends AbstractList<Element> {
    /**
     * @return A new Builder that can be used as a convenient method for creating an instance of the immutable
     *         {@link EnergyProfile}.
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * Represents the elements of an EnergyProfile.
     */
    public static class Element {
        private final Measurable<Duration> duration;
        private final Measurable<Energy> energy;

        Element(Measurable<Duration> duration, Measurable<Energy> energy) {
            if (duration == null || energy == null) {
                throw new NullPointerException();
            }
            this.duration = duration;
            this.energy = energy;
        }

        /**
         * @return The duration of this element
         */
        public Measurable<Duration> getDuration() {
            return duration;
        }

        /**
         * @return The amount of energy of this element
         */
        public Measurable<Energy> getEnergy() {
            return energy;
        }

        /**
         * @return The average power that has been produced or consumed during this period.
         */
        public Measurable<Power> getAveragePower() {
            double joules = energy.doubleValue(JOULE);
            double seconds = duration.doubleValue(SECOND);
            return Measure.valueOf(joules / seconds, WATT);
        }

        @Override
        public String toString() {
            return "(" + duration + "," + energy + ")";
        }
    }

    /**
     * This Builder can be used as a convenient method for creating an instance of the immutable {@link EnergyProfile}.
     */
    public static class Builder {
        private Measurable<Duration> duration;
        private final List<Element> profile;

        Builder() {
            duration = null;
            profile = new ArrayList<Element>();
        }

        /**
         * Adds an element using.
         * 
         * @param duration
         *            The duration for this element
         * @param energy
         *            The energy for this element
         * @return this
         */
        public Builder add(Measurable<Duration> duration, Measurable<Energy> energy) {
            profile.add(new Element(duration, energy));
            return this;
        }

        /**
         * Adds an element using the default duration as set by the {@link #setDuration(Measurable)} method.
         * 
         * @param energy
         *            The energy for this element
         * @return this
         */
        public Builder add(Measurable<Energy> energy) {
            profile.add(new Element(duration, energy));
            return this;
        }

        /**
         * Adds an element using the average amount of power in this element
         * 
         * @param duration
         *            The duration for this element
         * @param averagePower
         *            The average amount of power in this element
         * @return this
         */
        public Builder addPower(Measurable<Duration> duration, Measurable<Power> averagePower) {
            double powerW = averagePower.doubleValue(WATT);
            double durationS = duration.doubleValue(SECOND);
            profile.add(new Element(duration, Measure.valueOf(powerW * durationS, JOULE)));
            return this;
        }

        /**
         * Adds an element using the default duration as set by the {@link #setDuration(Measurable)} method.
         * 
         * @param averagePower
         *            The average amount of power in this element
         * @return this
         */
        public Builder addPower(Measurable<Power> averagePower) {
            this.addPower(duration, averagePower);
            return this;
        }

        /**
         * Sets the current duration that will be used in any subsequent calls to {@link #add(Measurable)}.
         * 
         * @param duration
         *            The new default duration
         * @return this
         */
        public Builder setDuration(Measurable<Duration> duration) {
            this.duration = duration;
            return this;
        }

        /**
         * @return The created immutable {@link EnergyProfile}.
         */
        public EnergyProfile build() {
            return new EnergyProfile(profile.toArray(new Element[profile.size()]));
        }
    }

    private final Element[] profile;

    /**
     * Creates a new EnergyProfile using the given values with a fixed duration for each element.
     * 
     * @param duration
     *            The duration that will be used for each element.
     * @param unit
     *            The unit in which the values are expressed.
     * @param values
     *            The values.
     */
    public EnergyProfile(Measurable<Duration> duration, Unit<Energy> unit, double... values) {
        profile = new Element[values.length];
        for (int ix = 0; ix < values.length; ix++) {
            profile[ix] = new Element(duration, Measure.valueOf(values[ix], unit));
        }
    }

    /**
     * Creates a new EnergyProfile using the given values with a fixed duration for each element.
     * 
     * @param duration
     *            The duration that will be used for each element.
     * @param values
     *            The values.
     */
    public EnergyProfile(Measurable<Duration> duration, Measurable<Energy>... values) {
        profile = new Element[values.length];
        for (int ix = 0; ix < values.length; ix++) {
            profile[ix] = new Element(duration, values[ix]);
        }
    }

    EnergyProfile(Element[] profile) {
        this.profile = profile;
    }

    /**
     * @return An array with all the elements to represent the whole profile.
     */
    public Element[] getProfile() {
        return Arrays.copyOf(profile, profile.length);
    }

    /**
     * @see #getElementForOffset(Measurable)
     * @param offset
     *            The offset in the profile for which to get the energy.
     * @return The value for a given offset.
     */
    public Measurable<Energy> getValueForOffset(Measurable<Duration> offset) {
        Element e = getElementForOffset(offset);
        return e == null ? null : e.getEnergy();
    }

    /**
     * @param offset
     *            The offset in the profile for which to get the energy.
     * @return The Element for a given offset.
     */
    public Element getElementForOffset(Measurable<Duration> offset) {
        double offsetDone = 0;
        double offsetNeeded = offset.doubleValue(Duration.UNIT);

        for (Element element : profile) {
            offsetDone += element.duration.doubleValue(Duration.UNIT);
            if (offsetDone >= offsetNeeded) {
                return element;
            }
        }

        return null;
    }

    /**
     * @return The total amount of energy that is represented by this profile.
     */
    public Measurable<Energy> getTotalEnergy() {
        double value = 0;
        for (Element part : profile) {
            value += part.getEnergy().doubleValue(JOULE);
        }
        return Measure.valueOf(value, JOULE);
    }

    /**
     * @return The total amount of duration that is stored in this profile.
     */
    public Measurable<Duration> getDuration() {
        double duration = 0;

        for (Element e : getProfile()) {
            duration += e.getDuration().doubleValue(SECOND);
        }

        return Measure.valueOf(duration, SECOND);
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
    @SuppressWarnings("unchecked")
    public EnergyProfile subprofile(Measurable<Duration> offset, Measurable<Duration> duration) {
        // duration must be specified
        if (duration == null) {
            throw new IllegalArgumentException("No duration provided for the subprofile");
        }

        // if the duration is zero, return an empty profile
        // or if the original profile is empty, return an empty profile
        if (duration.longValue(MILLI(SECOND)) == 0 || getDuration().longValue(MILLI(SECOND)) == 0) {
            return new EnergyProfile(duration);
        }

        // if there is no offset use 0
        if (offset == null) {
            offset = Measure.valueOf(0, SECOND);
        }

        // if the offset is larger than the profile, return an empty profile
        if (offset.compareTo(getDuration()) > 0) {
            return new EnergyProfile(duration);
        }

        EnergyProfile.Builder builder = new EnergyProfile.Builder();

        // the start, duration and end of the sub profile
        double subprofileStart = offset.doubleValue(SECOND);
        double subprofileDuration = duration.doubleValue(SECOND);
        double subprofileEnd = subprofileStart + subprofileDuration;

        // variables for the start, duration and end of a profile element
        double eStart = 0, eDuration, eEnd;

        // prepend if offset is negative
        if (subprofileStart < 0) {
            builder.add(Measure.valueOf(subprofileStart * -1, SECOND), Measure.valueOf(0, JOULE));
        }

        for (int i = 0; i < size(); i++) {
            Element e = get(i);
            eDuration = e.getDuration().doubleValue(SECOND);
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

                if (timeToSkip == 0) {
                    // add the full element
                    builder.add(e.getDuration(), e.getEnergy());
                } else {
                    // or add part of the element
                    double timeToKeep = eDuration - timeToSkip;
                    double energyValue = e.getEnergy().doubleValue(JOULE) * (timeToKeep / eDuration);
                    builder.add(Measure.valueOf(timeToKeep, SECOND), Measure.valueOf(energyValue, JOULE));
                }
            }

            eStart = eEnd;
        }

        // append empty element if duration is longer than profile
        double remainder = subprofileDuration - builder.build().getDuration().doubleValue(SECOND);
        if (remainder > 0) {
            builder.add(Measure.valueOf(remainder, SECOND), Measure.valueOf(0, JOULE));
        }

        // return the new profile
        return builder.build();
    }

    @Override
    public int hashCode() {
        int prime = 67;
        return prime * Arrays.hashCode(profile);
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
