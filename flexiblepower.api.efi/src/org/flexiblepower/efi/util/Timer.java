package org.flexiblepower.efi.util;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class Timer {
    private final int id;
    private final String label;
    private final Measurable<Duration> duration;

    public Timer(int id, String label, Measurable<Duration> duration) {
        if (label == null) {
            throw new NullPointerException("label");
        } else if (duration == null) {
            throw new NullPointerException("duration");
        }

        this.id = id;
        this.label = label;
        this.duration = duration;
    }

    /**
     * @return An unique identifiers for this timer within the context of a device.
     */
    public int getId() {
        return id;
    }

    /**
     * @return A human readable label for this timer. E.g. “on” timer.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return The (minimal) period of time that has to be respected before this timer is finished.
     */
    public Measurable<Duration> getDuration() {
        return duration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + duration.hashCode();
        result = prime * result + id;
        result = prime * result + label.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Timer other = (Timer) obj;
        if (!duration.equals(other.duration)) {
            return false;
        } else if (id != other.id) {
            return false;
        } else if (!label.equals(other.label)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Timer [id=" + id + ", label=" + label + ", duration=" + duration + "]";
    }
}
