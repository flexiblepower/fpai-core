package org.flexiblepower.api.efi.bufferhelper;

import java.util.Date;

/**
 * The implementation of the timer that can change when new updates are received and that provides information on
 * whether the timer is blocking or finished.
 *
 */
public class ActuatorTimer extends org.flexiblepower.efi.util.Timer {
    private Date finishedAt;

    /**
     * Constructs a new timer.
     *
     * @param base
     */
    public ActuatorTimer(org.flexiblepower.efi.util.Timer base) {
        super(base.getId(), base.getLabel(), base.getDuration());
    }

    /**
     * A new finishedAt moment overwrites the old moment.
     *
     * @param finishedAt
     */
    public void updateFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    /**
     * Returns the finishedAt moment.
     *
     * @return The moment at which the timer is finished.
     */
    public Date getFinishedAt() {
        return finishedAt;
    }

    /**
     * Returns whether the timer is blocking at this moment.
     *
     * @param moment
     * @return A boolean indicating whether the timer is blocking or not at the given moment.
     */
    public boolean isBlockingAt(Date moment) {
        return (finishedAt != null && finishedAt.after(moment));
    }
}
