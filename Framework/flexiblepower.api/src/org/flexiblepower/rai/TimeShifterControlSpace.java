package org.flexiblepower.rai;

import java.util.Date;

import org.flexiblepower.rai.values.EnergyProfile;

/**
 * TimeShifterControlSpace is a ControlSpace to expose energetic flexibility of TimeShifter resource.
 * <p>
 * TimeShifter resources consume/generate energy according to a predetermined energy profile, the start time of this
 * profile can be shifted in time over a given period. Examples: washing machine or dish washer that allows shifting the
 * start moment for executing its program.
 * <p>
 * Main parameters are: Energy profile of the program and allowed interval [startAfter,startBefore] for start moment of
 * program.
 * <p>
 * PMSuite - PM Control Specification - v0.6
 */
public final class TimeShifterControlSpace extends ControlSpace {

    /**
     * earliest time instant at which the resource wants to start its program
     */
    private final Date startAfter;

    /**
     * earliest time instant at which the resource wants to start its program
     */
    private final Date startBefore;

    /**
     * predetermined energy profile the resource managed by the creator will exhibit when executing its program
     */
    private final EnergyProfile energyProfile;

    /**
     * construct time shifter control space, which exposes the energy flexibility of a resource wanting to execute a
     * program with time shifting flexibility. For no flexibility, set startBefore and startAfter to the same time
     * instant.
     * 
     * @param resourceManager
     *            is the creator of the control space
     * @param validFrom
     *            is the start time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param validThru
     *            is the end time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param expirationTime
     *            is the time instant at which the creator will take autonomously action when no allocation is received
     *            by then. Is optional, provide null in the case it is not specified.
     * @param energyProfile
     *            is the predetermined energy profile the resource managed by the creator will exhibit
     * @param startBefore
     *            is the latest time instant at which the resource wants to start its program
     * @param startAfter
     *            is the earliest time instant at which the resource wants to start its program
     * 
     * @throws NullPointerException
     *             if energyProfile is null
     * @throws NullPointerException
     *             if startBefore is null
     * @throws NullPointerException
     *             if startAfter is null
     * @throws IllegalArgumentException
     *             if startAfter is after startBefore
     */
    public TimeShifterControlSpace(String applianceId,
                                   Date validFrom,
                                   Date validThru,
                                   Date expirationTime,
                                   EnergyProfile energyProfile,
                                   Date startBefore,
                                   Date startAfter) {
        super(applianceId, validFrom, validThru, expirationTime);
        this.energyProfile = energyProfile;
        this.startBefore = startBefore;
        this.startAfter = startAfter;
        validate();
    }

    public Date getStartAfter() {
        return startAfter;
    }

    public Date getStartBefore() {
        return startBefore;
    }

    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

    private void validate() {
        if (energyProfile == null) {
            throw new NullPointerException("energyProfile is null");
        }
        if (startBefore == null) {
            throw new NullPointerException("startBefore is null");
        }
        if (startAfter == null) {
            throw new NullPointerException("startAfter is null");
        }
        if (startAfter.after(startBefore)) {
            throw new IllegalArgumentException("startAfter (" + startAfter
                                               + ") is after startBefore ("
                                               + startBefore
                                               + ")");
        }
        // TODO -- could check here expiration time in relation with startAfter and startBefore
        // TODO -- could check that energyProfile must have a total energy > 0
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((energyProfile == null) ? 0 : energyProfile.hashCode());
        result = prime * result + ((startAfter == null) ? 0 : startAfter.hashCode());
        result = prime * result + ((startBefore == null) ? 0 : startBefore.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TimeShifterControlSpace other = (TimeShifterControlSpace) obj;
        if (energyProfile == null) {
            if (other.energyProfile != null) {
                return false;
            }
        } else if (!energyProfile.equals(other.energyProfile)) {
            return false;
        }
        if (startAfter == null) {
            if (other.startAfter != null) {
                return false;
            }
        } else if (!startAfter.equals(other.startAfter)) {
            return false;
        }
        if (startBefore == null) {
            if (other.startBefore != null) {
                return false;
            }
        } else if (!startBefore.equals(other.startBefore)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " {" + energyProfile + "}";
    }
}
