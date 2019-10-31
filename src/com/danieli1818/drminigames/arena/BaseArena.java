package com.danieli1818.drminigames.arena;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import com.danieli1818.drminigames.arena.kits.Kit;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.resources.api.arena.events.JoinEvent;
//import com.danieli1818.drminigames.api.Arena;
//import com.danieli1818.drminigames.api.ArenaLogic;
//import com.danieli1818.drminigames.api.arena.events.JoinEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;

public class BaseArena extends Observable implements Arena {
	
	private List<UUID> players;
	
	private Map<String, Location> spawnLocation;
	
	private Location waitingLocation;
	
	private Location leaveLocation;
	
	private Region limits;
	
	private int minNumPlayers;
	
	private int maxNumPlayers;
	
	private ArenaLogic al;
	
	private Timer timer;
	
	private long countdown;
	
	private List<Kit> kits;
	
	private enum GameState {
		UNAVAILABLE,
		WAITING,
		COOLDOWN,
		RUNNING
	}
	
	private GameState state;
	
	public BaseArena(String id) {
		this.players = Collections.synchronizedList(new ArrayList<UUID>());
		this.minNumPlayers = -1;
		this.maxNumPlayers = -1;
		this.state = GameState.UNAVAILABLE;
		this.limits = null;
		this.spawnLocation = new HashMap<String, Location>();
		this.timer = new Timer();
		this.countdown = 10;
		this.kits = Collections.synchronizedList(new ArrayList<Kit>());
		reset();
	}
	
	public boolean contains(UUID id) {
		return this.players.contains(id);
	}
	
	public boolean addPlayer(UUID id) {
		if (this.players.contains(id)) {
			return false;
		}
		if (this.state == GameState.RUNNING || this.state == GameState.UNAVAILABLE) {
			return false;
		}
		Player p = Bukkit.getPlayer(id);
		this.players.add(p.getUniqueId());
		this.al.update(this, new JoinEvent(p));
		p.teleport(this.waitingLocation);
		if (this.state != GameState.COOLDOWN && this.players.size() >= this.minNumPlayers) {
			this.state = GameState.COOLDOWN;
			Arena thisArena = this;
			startTimer(this.countdown, new TimerTask() {
				
				@Override
				public void run() {
					al.start(thisArena);
					
				}
			});
		}
		return true;
	}
	
	public boolean removePlayer(UUID id) {
		if (!this.players.contains(id)) {
			return false;
		}
		this.players.remove(id);
		if (this.players.isEmpty()) {
			finishGame();
		} else {
			if (this.state == GameState.COOLDOWN && this.players.size() < this.minNumPlayers) {
				this.state = GameState.WAITING;
			}
		}
		Player p = Bukkit.getPlayer(id);
		p.teleport(this.leaveLocation);
		return true;
	}
	
	public void reset() {
		this.players.clear();
		if (this.state != GameState.UNAVAILABLE) {
			if (this.state == GameState.COOLDOWN) {
				cancelTimer();
			}
			this.state = GameState.WAITING;
		}
	}
	
	public boolean isAvailable() {
		return this.state != GameState.UNAVAILABLE;
	}
	
	public boolean setAvailable() {
		if (!canBeAvailable()) {
			// message
			return false;
		}
		this.state = GameState.WAITING;
		return true;
	}
	
	public boolean setUnavailable() {
		if (this.state == GameState.RUNNING) {
			finishGame();
		} else {
			if (this.state == GameState.WAITING || this.state == GameState.COOLDOWN) {
				if (this.state == GameState.COOLDOWN) {
					cancelTimer();
				}
				this.state = GameState.UNAVAILABLE;
				kickAllPlayers();
			}
		}
		this.state = GameState.UNAVAILABLE;
		return true;
	}
	
	public void finishGame() {
		// finish
		kickAllPlayers();
		reset();
	}
	
	private boolean canBeAvailable() {
		if (this.players == null) {
			return false;
		}
		if (this.spawnLocation == null || this.spawnLocation.isEmpty()) {
			return false;
		}
		if (this.waitingLocation == null) {
			return false;
		}
		if (this.leaveLocation == null) {
			return false;
		}
		if (this.limits == null) {
			return false;
		}
		if (this.minNumPlayers == -1 || this.maxNumPlayers == -1) {
			return false;
		}
		if (this.al == null) {
			return false;
		}
		return this.al.canBeAvailable(this);
	}
	
	public boolean kickPlayer(UUID id) {
		if (!removePlayer(id)) {
			return false;
		}
		// message
		return true;
	}
	
	public void kickAllPlayers() {
		for (UUID id : this.players) {
			kickPlayer(id);
		}
	}
	
	private void startTimer(long delay, TimerTask timerTask) {
		// Timer
		this.timer.schedule(timerTask, delay);
	}
	
	private void cancelTimer() {
		// cancel timer
		this.timer.cancel();
	}
	
	public void setRegion(Region r) {
		this.limits = r;
		this.spawnLocation.clear();
	}
	
	public boolean setSpawnPoint(String name, Location location) {
		
		if (this.limits == null || !this.limits.contains(new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()))) {
			return false;
		}
		
//		boolean returnValue = this.spawnLocation.containsKey(name);
		this.spawnLocation.put(name, location);
		
		// message.
		
		return true;
	}
	
	public Map<String, Location> getSpawnPoints() {
		return this.spawnLocation;
	}
	
	public boolean setWaitingLocation(Location location) {
		this.waitingLocation = location;
		return true;
	}
	
	public boolean setLeavingLocation(Location location) {
		this.leaveLocation = location;
		return true;
		
	}
	
	public boolean setType(ArenaLogic al) {
		this.al = al;
		return true;
		
	}
	
	public boolean setCountdown(long countdown) {
		if (countdown >= 0) {
			this.countdown = countdown;
			return true;
		}
		return false;
	}

	@Override
	public List<UUID> getPlayers() {
		return this.players;
	}

	@Override
	public Map<String, Location> getSpawnLocation() {
		return this.spawnLocation;
	}

	@Override
	public Location getWaitingLocation() {
		return this.waitingLocation;
	}

	@Override
	public Location getLeaveLocation() {
		return this.leaveLocation;
	}

	@Override
	public Region getLimits() {
		return this.limits;
	}

	@Override
	public int getMinNumPlayers() {
		return this.minNumPlayers;
	}

	@Override
	public int getMaxNumPlayers() {
		return this.maxNumPlayers;
	}

	@Override
	public ArenaLogic getAL() {
		return this.al;
	}

	@Override
	public Timer getTimer() {
		return this.timer;
	}

	@Override
	public long getCountdown() {
		return this.countdown;
	}

	@Override
	public void addKit(Kit kit) {
		this.kits.add(kit);
		
	}

	@Override
	public void removeKit(Kit kit) {
		this.kits.remove(kit);
		
	}

	@Override
	public void removeAllKits() {
		this.kits.clear();
		
	}

}
