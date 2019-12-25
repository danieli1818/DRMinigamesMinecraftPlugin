package com.danieli1818.drminigames.common.configurationserializables;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.common.exceptions.ArgumentOutOfBoundsException;

public class Timer implements ConfigurationSerializable {

	private long timeInMiliSeconds;
	private long timeLeft;
	private Thread timerThread;
	private Consumer<Long> consumer;
	private long period;
	private long delay;
	private volatile Boolean shouldStop;
	private final Object shouldStopLock = new Object();
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
		synchronized(this.shouldStopLock) {
			this.shouldStop = false;
		}
		this.timerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized(shouldStopLock) {
						if (shouldStop) {
							return;
						}
						if (delay != 0) {
							shouldStopLock.wait(delay);
						}
					}
					
					timeLeft = timeInMiliSeconds;
					
					synchronized(consumer) {
						consumer.accept(timeLeft);
					}
					while (timeLeft - period > 0) {
						synchronized(shouldStopLock) {
							if (shouldStop) {
								return;
							}
							shouldStopLock.wait(period);
						}
						timeLeft -= period;
						synchronized(consumer) {
							consumer.accept(timeLeft);
						}
					}
					synchronized(shouldStopLock) {
						if (shouldStop) {
							return;
						}
						if (timeLeft != 0) {
							shouldStopLock.wait(timeLeft);
						}
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
		this.timerThread.start();
	}
	
	public long getTimeLeft() {
		return this.timeLeft;
	}
	
	public void stopTimer() {
		synchronized(this.shouldStopLock) {
			this.shouldStop = true;
			this.shouldStopLock.notifyAll();
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
	
	public long getTime() {
		return this.timeInMiliSeconds;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", timeInMiliSeconds);
		map.put("period", period);
		map.put("delay", delay);
		return map;
	}
	
	public static Timer deserialize(Map<String, Object> map) {
		Timer timer = new Timer();
		if (map.containsKey("time")) {
			timer.timeInMiliSeconds = (Integer)map.get("time");
		}
		if (map.containsKey("period")) {
			timer.period = (Integer)map.get("period");
		}
		if (map.containsKey("delay")) {
			timer.delay = (Integer)map.get("delay");
		}
		return timer;
	}
	
}
