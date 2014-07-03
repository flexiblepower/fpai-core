package org.flexiblepower.efi.buffer;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class Timer {

	private int id;
	private final String name;
	private final Measurable<Duration> duration;
	private final Date finishedAt;

	public Timer(String name, Measurable<Duration> duration, Date finishedAt) {
		super();
		this.name = name;
		this.duration = duration;
		this.finishedAt = finishedAt;
	}

	public String getName() {
		return name;
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
