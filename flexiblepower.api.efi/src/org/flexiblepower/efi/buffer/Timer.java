package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class Timer {

	private int id;
	private final String label;
	private final Measurable<Duration> duration;
	private final Date finishedAt;

	public Timer(String label, Measurable<Duration> duration, Date finishedAt) {
		super();
		this.label = label;
		this.duration = duration;
		this.finishedAt = finishedAt;
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

	public boolean timerIsFinished() {
		// TODO
		return true;
	}
}
