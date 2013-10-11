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
public class TimeShifterControlSpace extends ControlSpace {

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
     * Construct time shifter control space, which exposes the energy flexibility of a resource wanting to execute a
     * program with time shifting flexibility. For no flexibility, set startBefore and startAfter to the same time
     * instant.
     * 
     * @param resourceId
     *            is the identifier of the resource that created this control space.
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
     *             if energyProfile, startBefore or startAfter is null
     * @throws IllegalArgumentException
     *             if startAfter is after startBefore
     */
    public TimeShifterControlSpace(String resourceId,
                                   Date validFrom,
                                   Date validThru,
                                   Date expirationTime,
                                   EnergyProfile energyProfile,
                                   Date startBefore,
                                   Date startAfter) {
        super(resourceId, validFrom, validThru, expirationTime);
        this.energyProfile = energyProfile;
        this.startBefore = startBefore;
        this.startAfter = startAfter;

        if (energyProfile == null || startAfter == null || startBefore == null) {
            throw new NullPointerException();
        }
        if (startAfter.after(startBefore)) {
            throw new IllegalArgumentException("startAfter (" + startAfter
                                               + ") is after startBefore ("
                                               + startBefore
                                               + ")");
        }
    }

    /**
     * @return The earliest time instant at which the resource wants to start its program.
     */
    public Date getStartAfter() {
        return startAfter;
    }

    /**
     * @return The latest time instant at which the resource wants to start its program.
     */
    public Date getStartBefore() {
        return startBefore;
    }

    /**
     * @return The energy profile.
     */
    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + energyProfile.hashCode();
        result = prime * result + startAfter.hashCode();
        result = prime * result + startBefore.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        } else {
            TimeShifterControlSpace other = (TimeShifterControlSpace) obj;
            if (!energyProfile.equals(other.energyProfile)) {
                return false;
            } else if (!startAfter.equals(other.startAfter)) {
                return false;
            } else if (!startBefore.equals(other.startBefore)) {
                return false;
            }
            return true;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " {" + energyProfile + "}";
    }
}
