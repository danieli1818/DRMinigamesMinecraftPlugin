package com.danieli1818.drminigames.common;

import java.util.function.Consumer;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.common.exceptions.ArgumentOutOfBoundsException;

public class Timer {

	private long timeInMiliSeconds;
	private long timeLeft;
	private Thread timerThread;
	private Consumer<Long> consumer;
	private long period;
	private long delay;
	private Boolean shouldStop;
	private boolean hasStarted;
	
	public Timer(long time, long period, Consumer<Long> consumer, long delay) throws ArgumentOutOfBoundsException, NullArgumentException {
		if (time <= 0 || period <= 0 || period >= time || delay < 0) {
			throw new ArgumentOutOfBoundsException();
		}
		if (consumer == null) {
			throw new NullArgumentException("consumer");
		}
		this.timeInMiliSeconds = time;
		this.period = period;
		this.consumer = consumer;
		this.delay = delay;
		this.shouldStop = true;
		this.hasStarted = false;
	}
	
	public Timer(long time) throws ArgumentOutOfBoundsException {
		this(time, 1000, (Long num) -> {}, 0);
	}
	
	public Timer() {
		this.timeInMiliSeconds = 10000;
		this.period = 1000;
		this.delay = 0;
		this.shouldStop = true;
		this.hasStarted = false;
		this.consumer = (Long num) -> {};
	}
	
	public void start() {
		this.hasStarted = true;
		synchronized(this.shouldStop) {
			this.shouldStop = false;
		}
		this.timerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized(shouldStop) {
						if (shouldStop) {
							return;
						}
						shouldStop.wait(delay);
					}
					
					timeLeft = timeInMiliSeconds;
					
					synchronized(consumer) {
						consumer.accept(timeLeft);
					}
					while (timeLeft - period >= 0) {
						synchronized(shouldStop) {
							if (shouldStop) {
								return;
							}
							shouldStop.wait(period);
						}
						timeLeft -= period;
						synchronized(consumer) {
							consumer.accept(timeLeft);
						}
					}
					synchronized(shouldStop) {
						if (shouldStop) {
							return;
						}
						shouldStop.wait(timeLeft);
					}
					timeLeft = 0;
					synchronized(consumer) {
						consumer.accept(timeLeft);
					}
				} catch (InterruptedException e) {
					
				}
				hasStarted = false;
			}
		});
	}
	
	public long getTimeLeft() {
		return this.timeLeft;
	}
	
	public void stopTimer() {
		synchronized(this.shouldStop) {
			this.shouldStop = true;
		}
		if (this.timerThread != null && this.timerThread.isAlive()) {
			this.timerThread.interrupt();
		}
	}
	
	public void setTask(Consumer<Long> consumer) throws NullArgumentException {
		if (consumer == null) {
			throw new NullArgumentException("consumer");
		}
		stopTimer();
		synchronized(this.consumer) {
			this.consumer = consumer;
		}
	}
	
	public void setTime(long time) throws ArgumentOutOfBoundsException {
		if (time <= 0) {
			throw new ArgumentOutOfBoundsException();
		}
		stopTimer();
		this.timeInMiliSeconds = time;
	}
	
	public void setPeriod(long period) throws ArgumentOutOfBoundsException {
		if (period <= 0) {
			throw new ArgumentOutOfBoundsException();
		}
		stopTimer();
		this.period = period;
	}
	
	public void setDelay(long delay) throws ArgumentOutOfBoundsException {
		if (delay <= 0) {
			throw new ArgumentOutOfBoundsException();
		}
		stopTimer();
		this.delay = delay;
	}
	
}
