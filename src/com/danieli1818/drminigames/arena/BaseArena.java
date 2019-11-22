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
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.BlockVector;

import com.danieli1818.drminigames.arena.kits.Kit;
import com.danieli1818.drminigames.arena.kits.KitsManager;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.resources.api.arena.events.JoinEvent;
//import com.danieli1818.drminigames.api.Arena;
//import com.danieli1818.drminigames.api.ArenaLogic;
//import com.danieli1818.drminigames.api.arena.events.JoinEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class BaseArena extends Observable implements Arena {
	
	private final String id;
	
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
	
	private Map<String, Region> regions;
	
	private enum GameState {
		UNAVAILABLE,
		WAITING,
		COOLDOWN,
		RUNNING
	}
	
	private GameState state;
	
	public BaseArena(String id) {
		this.id = id;
		this.players = Collections.synchronizedList(new ArrayList<UUID>());
		this.minNumPlayers = -1;
		this.maxNumPlayers = -1;
		this.state = GameState.UNAVAILABLE;
		this.limits = null;
		this.spawnLocation = new HashMap<String, Location>();
		this.timer = new Timer();
		this.countdown = 10000;
		this.kits = Collections.synchronizedList(new ArrayList<Kit>());
		this.regions = new HashMap<String, Region>();
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
		notifyObservers(new JoinEvent(p));
		p.teleport(this.waitingLocation);
		if (this.state != GameState.COOLDOWN && this.players.size() >= this.minNumPlayers) {
			this.state = GameState.COOLDOWN;
			Arena thisArena = this;
			startTimer(this.countdown, new TimerTask() {
				
				@Override
				public void run() {
					state = GameState.RUNNING;
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
		if (this.timer != null) {
			this.timer.cancel();
		}
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
		if (this.minNumPlayers == -1 || this.maxNumPlayers == -1 || this.minNumPlayers > this.maxNumPlayers) {
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
	
	@Override
	public boolean setRegion(Region r, String id) {
		if (id == null) {
			boolean returnValue = this.limits != null;
			setRegion(r);
			return returnValue;
		}
		boolean returnValue = this.regions.containsKey(id);
		this.regions.put(id, r);
		return returnValue;
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

	@Override
	public Map<String, String> getArenaMap() {
				
		Map<String, String> arenaMap = new HashMap<String, String>();
		
		arenaMap.put("spawnLocation", locationMapToString(this.spawnLocation));
		
		arenaMap.put("waitingLocation", locationToString(this.waitingLocation));
		
		arenaMap.put("leaveLocation", locationToString(this.leaveLocation));
		
		arenaMap.put("limits", regionToString(this.limits));

		arenaMap.put("minNumOfPlayers", String.valueOf(this.minNumPlayers));
		
		arenaMap.put("maxNumOfPlayers", String.valueOf(this.maxNumPlayers));
		
		if (this.al != null) {
			arenaMap.put("arenaLogicID", this.al.getID());
		}
				
		arenaMap.put("countdown", String.valueOf(this.countdown));
		
		arenaMap.put("kits", kitListToString(this.kits));
		
		return arenaMap;
	}

	@Override
	public void loadArenaFromMap(Map<String, String> arenaMap) {
		
		this.spawnLocation = locationsMapFromString(arenaMap.get("spawnLocation"));
		
		this.waitingLocation = getLocationFromString(arenaMap.get("waitingLocation"));
		
		this.leaveLocation = getLocationFromString(arenaMap.get("leaveLocation"));
		
		this.limits = regionFromString(arenaMap.get("limits"));
		
		try {
			this.minNumPlayers = Integer.parseInt(arenaMap.get("minNumOfPlayers"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			this.minNumPlayers = 4;
		}
		
		try {
			this.maxNumPlayers = Integer.parseInt(arenaMap.get("maxNumOfPlayers"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			this.maxNumPlayers = 40;
		}
		
		this.al = ArenasLogicsManager.loadArenaLogic(this, arenaMap.get("arenaLogicID"));
		
		try {
			this.countdown = Integer.parseInt(arenaMap.get("countdown"));
		} catch(NumberFormatException e) {
			e.printStackTrace();
			this.countdown = 10;
		}
		
		this.kits = kitListFromString(arenaMap.get("kits"));
		
		
	}
	
	private String locationMapToString(Map<String, Location> locationsMap) {
		if (locationsMap == null) {
			return "";
		}
		return locationsMap.entrySet().stream().map(entry -> entry.getKey() + "=" + locationToString(entry.getValue())).collect(Collectors.joining(", "));
	}
	
	private String locationToString(Location location) {
		if (location == null) {
			return "";
		}
		return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ();
	}
	
	private Location getLocationFromString(String locationString) {
		if (locationString == null || locationString.equals("")) {
			return null;
		}
		String[] locationCoords = locationString.split(":");
		if (locationCoords.length != 4) {
			return null;
		}
		try {
			return new Location(Bukkit.getServer().getWorld(locationCoords[0]), Double.parseDouble(locationCoords[1]), Double.parseDouble(locationCoords[2]), Double.parseDouble(locationCoords[3]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	private Map<String, Location> locationsMapFromString(String locationsMapString) {
		if (locationsMapString == null) {
			return new HashMap<String, Location>();
		}
		String[] entries = locationsMapString.split(", ");
		Map<String, Location> locationsMap = new HashMap<String, Location>();
		for (String entryString : entries) {
			String[] entry = entryString.split("=");
			if (entry.length != 2) {
				continue;
			}
			locationsMap.put(entry[0], getLocationFromString(entry[1]));
		}
		return new HashMap<String, Location>();
	}
	
	private String regionToString(Region region) {
		if (region == null) {
			return "";
		}
		return region.getWorld().getName() + ":" + region.getMinimumPoint().toString() + ":" + region.getMaximumPoint().toString();
	}
	
	private Region regionFromString(String regionString) {
		if (regionString == null || regionString.equals("")) {
			return null;
		}
		String[] regionProperties = regionString.split(":");
		if (regionProperties.length != 3) {
			return null;
		}
		World world = Bukkit.getServer().getWorld(regionProperties[0]);
		if (world == null) {
			return null;
		}
		Vector v1 = getVectorFromString(regionProperties[1]);
		if (v1 == null) {
			return null;
		}
		Vector v2 = getVectorFromString(regionProperties[2]);
		if (v2 == null) {
			return null;
		}
		com.sk89q.worldedit.world.World worldeditWorld = BukkitUtil.getLocalWorld(world);
		if (worldeditWorld == null) {
			return null;
		}
		return new CuboidRegion(worldeditWorld, v1, v2);
	}
	
	private Vector getVectorFromString(String vectorString) {
		if (vectorString == null) {
			return null;
		}
		if (!(vectorString.startsWith("(") && vectorString.endsWith(")"))) {
			return null;
		}
		vectorString = vectorString.substring(1, vectorString.length() - 1);
		String[] coords = vectorString.split(", ");
		if (coords.length != 3) {
			return null;
		}
		try {
			return new Vector(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
		} catch (NumberFormatException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	private String kitListToString(List<Kit> kitList) {
		if (kitList == null) {
			return "";
		}
		return kitList.stream().map(kit -> kit.getID()).collect(Collectors.joining(", "));
	}
	
	private List<Kit> kitListFromString(String kitListString) {
		if (kitListString == null || kitListString.equals("")) {
			return new ArrayList<Kit>();
		}
		String[] kitsIDs = kitListString.split(", ");
		ArrayList<Kit> kits = new ArrayList<Kit>();
		for (String kitID : kitsIDs) {
			kits.add(KitsManager.loadKit(kitID));
		}
		return kits;
	}

	@Override
	public boolean sendEvent(Event event) {
		notifyObservers(event);
		return true;
	}
	
	public Map<String, Region> getRegions() {
		return this.regions;
	}
	
	public boolean setMinNumOfPlayers(int num) {
		if (num <= 0) {
			return false;
		}
		this.minNumPlayers = num;
		return true;
	}
	
	public boolean setMaxNumOfPlayers(int num) {
		if (num <= 0) {
			return false;
		}
		this.maxNumPlayers = num;
		return true;
	}
	
	public boolean stop() {
		cancelTimer();
		return this.al.stop();
	}
	
	public void forceStop() {
		cancelTimer();
		this.al.forceStop();
	}
	
	public void sendCommand(Player player, String[] args) {
		this.al.command(player, args);
	}
	
	public String getID() {
		return this.id;
	}

}
