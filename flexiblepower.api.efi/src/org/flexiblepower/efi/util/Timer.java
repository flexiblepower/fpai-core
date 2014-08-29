package org.flexiblepower.efi.util;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.time.TimeService;

public class Timer {

    private final int id;
    private final String label;
    /** Total duration of this timer. It includes the transition period. */
    private final Measurable<Duration> duration;
    private Date finishedAt;

    public Timer(int id, String label, Measurable<Duration> duration, Date finishedAt) {
        super();
        this.id = id;
        this.label = label;
        this.duration = duration;
        this.finishedAt = finishedAt;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Measurable<Duration> getDuration() {
        return duration;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public boolean timerIsFinished(TimeService timeService) {
        return finishedAt.getTime() <= timeService.getCurrentTimeMillis();
    }

    public void updateFinishedAt(Date finishedAt2) {
        finishedAt = finishedAt2;
    }
}
