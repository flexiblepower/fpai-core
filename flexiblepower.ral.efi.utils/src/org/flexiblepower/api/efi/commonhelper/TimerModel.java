package org.flexiblepower.api.efi.commonhelper;

import java.util.Date;

import org.flexiblepower.efi.util.Timer;

/**
 * The implementation of the timer that can change when new updates are received and that provides information on
 * whether the timer is blocking or finished.
 */
public class TimerModel extends Timer {
    private Date finishedAt;

    /**
     * Constructs a new timer.
     *
     * @param base
     *            The object that holds the information from the EFI message about the Timer.
     */
    public TimerModel(Timer base) {
        super(base.getId(), base.getLabel(), base.getDuration());
    }

    /**
     * A new finishedAt moment overwrites the old moment.
     *
     * @param finishedAt
     *            The new date at which the timer should be finished.
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
     * Returns whether the timer is blocking at this moment. No finishedAt time implies that this timer is not blocking.
     *
     * @param moment The timestamp for which we should check.
     * @return A boolean indicating whether the timer is blocking or not at the given moment.
     */
    public boolean isBlockingAt(Date moment) {
        if (moment == null) {
            return false;
        }
        return (finishedAt != null && finishedAt.after(moment));
    }
}
