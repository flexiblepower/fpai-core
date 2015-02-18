package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.quantity.Quantity;

import org.flexiblepower.ral.values.ConstraintProfile;

/**
 * This message is being used to communicate a target profile that has to be met by this buffer. It is important to
 * stress that a new target profile message completely overrules a previous one.
 *
 * @param <Q>
 *            The quantity that describes what is stored in the buffer
 */
public class BufferTargetProfileUpdate<Q extends Quantity> extends BufferUpdate {
    private final ConstraintProfile<Q> targetProfile;

    /**
     * Constructs a new {@link BufferTargetProfileUpdate} message with the specific validFrom
     *
     * @param bufferRegistration
     *            The registration message to which this update is referring
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid. This also indicates the starttime
     *            of the profile
     * @param targetProfile
     *            The actual target profile that should be met by this buffer.
     */
    public BufferTargetProfileUpdate(BufferRegistration<Q> bufferRegistration,
                                     Date timestamp,
                                     Date validFrom,
                                     ConstraintProfile<Q> targetProfile) {
        super(bufferRegistration.getResourceId(), timestamp, validFrom);
        if (targetProfile == null) {
            throw new NullPointerException("targetProfile");
        }

        this.targetProfile = targetProfile;
    }

    /**
     * @return The actual target profile that should be met by this buffer.
     */
    public ConstraintProfile<Q> getTargetProfile() {
        return targetProfile;
    }
}
