package org.flexiblepower.rai;

import java.util.Date;

import org.flexiblepower.rai.values.EnergyProfile;

/**
 * UncontrolledLGControlSpace is a ControlSpace to expose energetic flexibility of Uncontrolled Load Generation
 * resource.
 * <p>
 * The Uncontrolled Load/Generation category are devices for which the energy behavior cannot be actively controlled.
 * One could make forecasts. Examples are: PV (Photo-voltaic) panels, television sets, computers, lighting.
 * <p>
 * This control space specifies a forecast, expressed as an energy profile and a start time.
 * <p>
 * Note that an EnergyProfile has EnergyOverDuration values which have optional confidenceintervals. This can be used to
 * indicate confidence of the forecasted profile.
 * 
 * PMSuite - PM Control Specification - v0.6
 */
public final class UncontrolledLGControlSpace extends ControlSpace {

    private final Date startTime;
    private final EnergyProfile energyProfile;

    /**
     * construct uncontrolledLG control space, which exposes the energy flexibility of an uncontrolled LG resource.
     * 
     * @param resourceManager
     *            is the creator of the control space
     * @param validFrom
     *            is the start time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param validThru
     *            is the end time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param expirationTime
     *            is the time after which the creator will autonomously act when no allocation was received by then. Is
     *            optional, one can provide null if not specified.
     * @param startTime
     *            is the start time of the forecast energyProfile
     * @param energyProfile
     *            is the forecast energy profile
     * 
     * 
     * @throws NullPointerException
     *             if startTime is null
     * @throws NullPointerException
     *             if energyProfile is null
     */
    public UncontrolledLGControlSpace(String applianceId,
                                      Date validFrom,
                                      Date validThru,
                                      Date expirationTime,
                                      Date startTime,
                                      EnergyProfile energyProfile) {
        super(applianceId, validFrom, validThru, expirationTime);
        this.startTime = startTime;
        this.energyProfile = energyProfile;
        validate();
    }

    public Date getStartTime() {
        return startTime;
    }

    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

    private void validate() {
        if (startTime == null) {
            throw new NullPointerException("startTime is null");
        }
        if (energyProfile == null) {
            throw new NullPointerException("energyProfile is null");
        }
        // TODO -- check relation between startTime and expirationTime
        // TODO -- option to check for total energy of energy profile, which should be >0
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((energyProfile == null) ? 0 : energyProfile.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
        UncontrolledLGControlSpace other = (UncontrolledLGControlSpace) obj;
        if (energyProfile == null) {
            if (other.energyProfile != null) {
                return false;
            }
        } else if (!energyProfile.equals(other.energyProfile)) {
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

}
