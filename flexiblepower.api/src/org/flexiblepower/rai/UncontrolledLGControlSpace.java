package org.flexiblepower.rai;

import java.util.Date;

import org.flexiblepower.rai.values.EnergyProfile;
import org.flexiblepower.time.TimeUtil;

/**
 * UncontrolledLGControlSpace is a ControlSpace to expose energetic flexibility of Uncontrolled Load Generation
 * resource. The Uncontrolled Load/Generation category are devices for which the energy behavior cannot be actively
 * controlled. One could make forecasts. Examples are: PV (Photo-voltaic) panels, television sets, computers, lighting.
 * This control space specifies a forecast, expressed as an energy profile and a start time.
 */
public class UncontrolledLGControlSpace extends ControlSpace {
    private final Date startTime;
    private final EnergyProfile energyProfile;

    /**
     * Creates a new uncontrolled control space, which exposes the energy profile of an uncontrolled resource.
     * 
     * The validFrom and validThru will be calculated using the startTime and length of the enery profile.
     * 
     * @param resourceId
     *            is the identifier of the resource that created this control space.
     * @param startTime
     *            is the start time of the forecast energyProfile.
     * @param energyProfile
     *            is the forecast energy profile.
     * 
     * 
     * @throws NullPointerException
     *             if startTime or the energyProfile is null
     * @see ControlSpace#ControlSpace(String, Date, Date)
     */
    public UncontrolledLGControlSpace(String resourceId, Date startTime, EnergyProfile energyProfile) {
        super(resourceId, startTime, TimeUtil.add(startTime, energyProfile.getDuration()));
        this.startTime = startTime;
        this.energyProfile = energyProfile;

        // TODO -- check relation between startTime and expirationTime
        // TODO -- option to check for total energy of energy profile, which should be >0
    }

    /**
     * @return The start time of the energy profile.
     */
    public Date getStartTime() {
        return startTime;
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
        result = prime * result + ((energyProfile == null) ? 0 : energyProfile.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        } else {
            UncontrolledLGControlSpace other = (UncontrolledLGControlSpace) obj;
            if (!energyProfile.equals(other.energyProfile)) {
                return false;
            } else if (!startTime.equals(other.startTime)) {
                return false;
            }
            return true;
        }
    }
}
