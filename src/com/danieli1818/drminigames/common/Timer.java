package com.danieli1818.drminigames.common;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.common.exceptions.ArgumentOutOfBoundsException;

public class Timer {

	private long timeInMiliSeconds;
	private long timeLeft;
	private Thread timerThread;
	private Runnable runnable;
	private long period;
	private long delay;
	private Boolean shouldStop;
	
	public Timer(long time, long period, Runnable runnable, long delay) throws ArgumentOutOfBoundsException, NullArgumentException {
		if (time <= 0 || period <= 0 || period >= time || delay < 0) {
			throw new ArgumentOutOfBoundsException();
		}
		if (runnable == null) {
			throw new NullArgumentException("runnable");
		}
		this.timeInMiliSeconds = time;
		this.timeLeft = this.timeInMiliSeconds;
		this.period = period;
		this.runnable = runnable;
		this.delay = delay;
	}
	
	public void start() {
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
					Bukkit.getScheduler().scheduleSyncDelayedTask(DRMinigames.getPlugin(DRMinigames.class), runnable);
					timeLeft = timeInMiliSeconds;
					while (timeLeft - period >= 0) {
						synchronized(shouldStop) {
							if (shouldStop) {
								return;
							}
							shouldStop.wait(period);
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(DRMinigames.getPlugin(DRMinigames.class), runnable);
						timeLeft -= period;
					}
					synchronized(shouldStop) {
						if (shouldStop) {
							return;
						}
						shouldStop.wait(timeLeft);
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(DRMinigames.getPlugin(DRMinigames.class), runnable);
					timeLeft = 0;
				} catch (InterruptedException e) {
					
				}
			}
		});
	}
	
	public long getTimeLeft() {
		return this.timeLeft;
	}
	
	public void stopTimer() {
		if (this.timerThread != null && this.timerThread.isAlive()) {
			this.timerThread.interrupt();
		}
	}
	
}
