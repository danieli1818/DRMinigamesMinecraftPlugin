package com.danieli1818.drminigames.arena;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockVector;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.arena.kits.Kit;
import com.danieli1818.drminigames.arena.kits.KitsManager;
import com.danieli1818.drminigames.common.configurationserializables.Timer;
import com.danieli1818.drminigames.common.exceptions.ArgumentOutOfBoundsException;
import com.danieli1818.drminigames.common.exceptions.InvalidConfigurationDataException;
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
		
	private List<Kit> kits;
	
	private Map<String, Region> regions;
	
	private Scoreboard board;
	
	private Timer countdownTimer;
	
	
	
	private enum GameState {
		UNAVAILABLE,
		WAITING,
		COUNTDOWN,
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
		this.kits = Collections.synchronizedList(new ArrayList<Kit>());
		this.regions = new HashMap<String, Region>();
		initializeScoreboard();
		this.countdownTimer = new Timer();
		this.countdownTimer.setTask((Long time) -> {
			onTimeUpdated(time);
		});
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
		this.board.getTeam("players").addPlayer(p);
		p.setScoreboard(this.board);
		if (this.state != GameState.COUNTDOWN && this.players.size() >= this.minNumPlayers) {
			this.state = GameState.COUNTDOWN;
			Arena thisArena = this;
			updateScoreboard(null);
			this.countdownTimer.start();
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
			if (this.state == GameState.COUNTDOWN && this.players.size() < this.minNumPlayers) {
				this.state = GameState.WAITING;
				this.countdownTimer.stopTimer();
			}
		}
		Player p = Bukkit.getPlayer(id);
		p.teleport(this.leaveLocation);
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		return true;
	}
	
	public void reset() {
		this.players.clear();
		if (this.state != GameState.UNAVAILABLE) {
			if (this.state == GameState.COUNTDOWN) {
				this.countdownTimer.stopTimer();
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
			if (this.state == GameState.WAITING || this.state == GameState.COUNTDOWN) {
				if (this.state == GameState.COUNTDOWN) {
					this.countdownTimer.stopTimer();
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
		while (!this.players.isEmpty()) {
			kickPlayer(this.players.get(0));
		}
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
		addObserver(al);
		return true;
		
	}
	
	public boolean setCountdown(long countdown) {
		try {
			this.countdownTimer.setTime(countdown);
			return true;
		} catch (ArgumentOutOfBoundsException e) {
			return false;
		}
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
	public long getCountdown() {
		return this.countdownTimer.getTime();
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
	
	public static Location getLocationFromString(String locationString) {
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
	
	public static Map<String, Location> locationsMapFromString(String locationsMapString) {
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
	
	public static Region regionFromString(String regionString) {
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
	
	public static Vector getVectorFromString(String vectorString) {
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
	
	private static List<Kit> kitListFromString(String kitListString) {
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
		setChanged();
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
	
	public boolean isRunning() {
		return this.state == GameState.RUNNING;
	}
	
	public boolean setMaxNumOfPlayers(int num) {
		if (num <= 0) {
			return false;
		}
		this.maxNumPlayers = num;
		return true;
	}
	
	public boolean stop() {
		this.countdownTimer.stopTimer();
		setUnavailable();
		return this.al.stop();
	}
	
	public void forceStop() {
		stop();
		this.al.forceStop();
	}
	
	public void sendCommand(Player player, String[] args) {
		this.al.command(player, args);
	}
	
	public String getID() {
		return this.id;
	}
	
	private void initializeScoreboard() {
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = this.board.registerNewObjective("status", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName("Minigame\n");
		this.board.registerNewTeam("players");
	}
	
	private String getStateString() {
		if (this.state == GameState.COUNTDOWN) {
			return "Countdown";
		} else if (this.state == GameState.RUNNING) {
			return "Running";
		} else if (this.state == GameState.UNAVAILABLE) {
			return "Unavailable";
		} else {
			return "Waiting";
		}
	}
	
	private String getStatusString(Long time) {
		String result;
		if (this.al != null) {
			result = this.al.getID();
		} else {
			result = "DRMinigame";
		}
		result += "\n";
		if (this.state != GameState.COUNTDOWN) {
			result += getStateString();
		} else {
			if (time != null) {
				result += time;
			} else {
				result += "NaN";
			}
		}
		
		return result;
	}
	
	private void updateScoreboard(Long time) {
		Objective o = this.board.getObjective("status");
		o.setDisplayName(getStatusString(time));
	}

	private void onTimeUpdated(long time) {
		if (time <= 0) {
			this.state = GameState.RUNNING;
			al.start(this);
		} else {
			updateScoreboard(time);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		
		Map<String, Object> arenaMap = new HashMap<String, Object>();
		
		arenaMap.put("id", this.id);
		
		arenaMap.put("spawnLocation", this.spawnLocation);
		
		arenaMap.put("waitingLocation", this.waitingLocation);
		
		arenaMap.put("leaveLocation", this.leaveLocation);
		
		arenaMap.put("limits", regionToString(this.limits));
		
		arenaMap.put("minNumOfPlayers", this.minNumPlayers);
		
		arenaMap.put("maxNumOfPlayers", this.maxNumPlayers);
		
		try {
			ArenasLogicsManager.saveArenaLogic(this.al, this.getID());
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		arenaMap.put("countdown", this.countdownTimer);
		
		arenaMap.put("kits", kitListToString(this.kits));
		
		return arenaMap;
	
	}
	
	public static BaseArena deserialize(Map<String, Object> map) {
		
		if (!map.containsKey("id")) {
			
			throw new InvalidConfigurationDataException("ID Key Value Not Found!");
			
		}
		
		BaseArena arena = new BaseArena((String)map.get("id"));
		
		if (map.containsKey("spawnLocation")) {
			arena.spawnLocation = locationsMapFromString((String)map.get("spawnLocation"));
		}
		
		if (map.containsKey("waitingLocation")) {
			arena.waitingLocation = getLocationFromString((String)map.get("waitingLocation"));
		}
		
		if (map.containsKey("leaveLocation")) {
			arena.leaveLocation = getLocationFromString((String)map.get("leaveLocation"));
		}
		
		if (map.containsKey("limits")) {
			arena.limits = regionFromString((String)map.get("limits"));
		}
		
		if (map.containsKey("minNumOfPlayers")) {
			try {
				arena.minNumPlayers = Integer.parseInt((String)map.get("minNumOfPlayers"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				arena.minNumPlayers = 4;
			}
		}
		
		if (map.containsKey("maxNumOfPlayers")) {
			try {
				arena.maxNumPlayers = Integer.parseInt((String)map.get("maxNumOfPlayers"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				arena.maxNumPlayers = 40;
			}
		}
		
		if (map.containsKey("arenaLogicID")) {
			arena.al = ArenasLogicsManager.loadArenaLogic(arena, arena.getID());
		}
		
		if (map.containsKey("countdown")) {
			try {
				arena.countdownTimer = (Timer)map.get("countdown");
				arena.countdownTimer.setTask((Long time) -> {
					arena.onTimeUpdated(time);
				});
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		if (map.containsKey("kits")) {
			arena.kits = kitListFromString((String)map.get("kits"));
		}
		
		return arena;
	}
	
}
