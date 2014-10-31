package org.flexiblepower.efi.timeshifter;

import java.util.Date;

public final class SequentialProfileAllocation {
    private final Date startTime;
    private final int sequentialProfileId;

    public SequentialProfileAllocation(int sequentialProfileId, Date startTime) {
        this.sequentialProfileId = sequentialProfileId;
        this.startTime = startTime;
    }

    /**
     * @return The unique id of the sequential profile that is allocated.
     */
    public int getSequentialProfileId() {
        return sequentialProfileId;
    }

    /**
     * @return The desired start time of allocation.
     */
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sequentialProfileId;
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SequentialProfileAllocation other = (SequentialProfileAllocation) obj;
        if (sequentialProfileId != other.sequentialProfileId) {
            return false;
        }
        if (startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SequentialProfileAllocation [startTime=" + startTime
               + ", sequentialProfileId="
               + sequentialProfileId
               + "]";
    }
}
