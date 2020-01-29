package com.danieli1818.drminigames.arena;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockVector;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.arena.kits.Kit;
import com.danieli1818.drminigames.arena.kits.KitIcon;
import com.danieli1818.drminigames.arena.kits.KitsManager;
import com.danieli1818.drminigames.common.TextScoreboard;
import com.danieli1818.drminigames.common.configurationserializables.Timer;
import com.danieli1818.drminigames.common.exceptions.ArgumentOutOfBoundsException;
import com.danieli1818.drminigames.common.exceptions.InvalidConfigurationDataException;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.resources.api.arena.events.JoinEvent;
import com.danieli1818.drminigames.utils.guis.GUIHolder;
import com.danieli1818.drminigames.utils.guis.SelectionGUIHolder;
import com.danieli1818.drminigames.utils.items.Action;
import com.danieli1818.drminigames.utils.items.CustomItemStack;
//import com.danieli1818.drminigames.api.Arena;
//import com.danieli1818.drminigames.api.ArenaLogic;
//import com.danieli1818.drminigames.api.arena.events.JoinEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class BaseArena extends Observable implements Arena {
	
	private final String id;
	
	private Map<String, Location> spawnLocation;
	
	private Location waitingLocation;
	
	private Location leaveLocation;
	
	private Region limits;
	
	private int minNumPlayers;
	
	private int maxNumPlayers;
	
	private ArenaLogic al;
		
	private List<Kit> kits;
	
	private Map<String, Region> regions;
	
	private TextScoreboard board;
	
	private Timer countdownTimer;
	
	private SelectionGUIHolder guiHolder;
	
	private Map<UUID, Kit> currentPlayersKits;
	
	private Map<UUID, ItemStack[]> playersInventories;
	
	private enum GameState {
		UNAVAILABLE,
		LOADING,
		WAITING,
		COUNTDOWN,
		RUNNING
	}
	
	private GameState state;
	
	public BaseArena(String id) {
		this.id = id;
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
		initializeGUIHolder();
		this.currentPlayersKits = new HashMap<>();
		this.playersInventories = new HashMap<>();
		reset();
	}
	
	public boolean contains(UUID id) {
		return getPlayers().contains(id);
	}
	
	public boolean addPlayer(UUID id) {
		if (!canAddPlayer(id)) {
			return false;
		}
		Player p = Bukkit.getPlayer(id);
		this.board.getTeam("players").addPlayer(p);
		notifyObservers(new JoinEvent(p));
		p.teleport(this.waitingLocation);
		this.board.setScoreboardToPlayer(p);
		if (this.state != GameState.COUNTDOWN && getPlayers().size() >= this.minNumPlayers) {
			this.state = GameState.COUNTDOWN;
			Arena thisArena = this;
			updateScoreboard(null);
			this.countdownTimer.start();
		}
		savePlayerInventory(p);
		givePlayerWaitingItems(p);
		return true;
	}
	
	private boolean canAddPlayer(UUID id) {
		if (getPlayers().size() >= this.maxNumPlayers || getPlayers().contains(id)) {
			return false;
		}
		return this.state == GameState.WAITING || this.state == GameState.COUNTDOWN;
	}
	
	@Override
	public boolean removePlayer(UUID id) {
		if (!getPlayers().contains(id)) {
			return false;
		}
		this.board.getTeam("players").removePlayer(Bukkit.getOfflinePlayer(id));
		if (getPlayers().isEmpty()) {
			finishGame();
		} else {
			if (this.state == GameState.COUNTDOWN && getPlayers().size() < this.minNumPlayers) {
				this.state = GameState.WAITING;
				this.countdownTimer.stopTimer();
			}
		}
		Player p = Bukkit.getPlayer(id);
		p.teleport(this.leaveLocation);
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		loadPlayerInventory(p);
		return true;
	}
	
	public void reset() {
		kickAllPlayers();
		if (this.state != GameState.UNAVAILABLE && this.state != GameState.LOADING) {
			if (this.state == GameState.COUNTDOWN) {
				this.countdownTimer.stopTimer();
			}
			this.state = GameState.WAITING;
		} else {
			this.state = GameState.UNAVAILABLE;
		}
		resetGUIHolder();
	}
	
	public boolean isAvailable() {
		return this.state != GameState.UNAVAILABLE && this.state != GameState.LOADING;
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
		if (getPlayers() == null) {
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
		while (!getPlayers().isEmpty()) {
			kickPlayer(getPlayers().get(0));
		}
	}
		
	public void setRegion(Region r) {
		System.out.println("Before Set Region:");
		printRegions();
		this.limits = r;
		this.spawnLocation.clear();
		System.out.println("After Set Region:");
		printRegions();
	}
	
	@Override
	public boolean setRegion(Region r, String id) {
		System.out.println("Before:");
		printRegions();
		if (id == null) {
			boolean returnValue = this.limits != null;
			setRegion(r);
			return returnValue;
		}
		boolean returnValue = this.regions.containsKey(id);
		this.regions.put(id, r);
		System.out.println("After:");
		printRegions();
		return returnValue;
	}
	
	private void printRegions() {
		System.out.println("Limits:");
		System.out.println(regionToString(this.limits));
		System.out.println("Regions:");
		for (Entry<String, Region> entry : this.regions.entrySet()) {
			System.out.println(entry.getKey() + ":");
			System.out.println(regionToString(entry.getValue()));
		}
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
		if (al == null) {
			return false;
		}
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
		return this.board.getTeam("players").getPlayers().stream().map((OfflinePlayer player) -> {
			return player.getUniqueId();
		}).collect(Collectors.toList());
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
		if (kit == null) {
			return;
		}
		this.kits.add(kit);
		resetGUIHolder();
	}

	@Override
	public boolean removeKit(Kit kit) {
		if (kit == null) {
			return false;
		}
		boolean returnValue = this.kits.remove(kit);
		resetGUIHolder();
		return returnValue;
	}

	@Override
	public void removeAllKits() {
		this.kits.clear();
		resetGUIHolder();
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
			kits.add(KitsManager.getInstance().loadKit(kitID));
		}
		return kits;
	}

	@Override
	public boolean sendEvent(Event event) {
		if (this.state == GameState.COUNTDOWN || this.state == GameState.WAITING) {
			if (event instanceof PlayerDropItemEvent) {
				PlayerDropItemEvent playerDropItemEvent = (PlayerDropItemEvent)event;
				playerDropItemEvent.setCancelled(true);
			}
		}
		if (event instanceof PlayerQuitEvent || event instanceof PlayerKickEvent) {
			PlayerEvent playerEvent = (PlayerEvent)event;
			removePlayer(playerEvent.getPlayer().getUniqueId());
		}
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
		this.board = new TextScoreboard("DRMinigame").setLine(1, "status:");
		this.board.registerNewTeam("players");
	}
	
	private String getStateString() {
		if (this.state == GameState.COUNTDOWN) {
			return "Countdown";
		} else if (this.state == GameState.RUNNING) {
			return "Running";
		} else if (this.state == GameState.UNAVAILABLE || this.state == GameState.LOADING) {
			return "Unavailable";
		} else {
			return "Waiting";
		}
	}
	
	private String getStatusString(Long time) {
		String result = getStateString();
//		if (this.al != null) {
//			result = this.al.getID();
//		} else {
//			result = "DRMinigame";
//		}
//		result += "\n";
		if (this.state != GameState.COUNTDOWN) {
//			result += getStateString();
		} else {
			result += ": ";
			if (time != null) {
				result += time / 1000;
			} else {
				result += "NaN";
			}
		}
		
		return result;
	}
	
	private void updateScoreboard(Long time) {
		this.board.setLine(2, getStatusString(time));
	}

	private void onTimeUpdated(long time) {
		if (time <= 0) {
			this.state = GameState.RUNNING;
			onStart();
			al.start(this);
		} else {
			updateScoreboard(time);
		}
	}
	
	private void onStart() {
		giveKitsToPlayers();
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
		
		arenaMap.put("regions", getNamedRegionsAsStrings());
		
		arenaMap.put("isAvailable", isAvailable());
		
		return arenaMap;
	
	}
	
	public static BaseArena deserialize(Map<String, Object> map) {
		
		if (!map.containsKey("id")) {
			
			throw new InvalidConfigurationDataException("ID Key Value Not Found!");
			
		}
		
		BaseArena arena = new BaseArena((String)map.get("id"));
		
		if (map.containsKey("spawnLocation")) {
			arena.spawnLocation = (Map<String, Location>) map.get("spawnLocation");
		}
		
		if (map.containsKey("waitingLocation")) {
			arena.waitingLocation = (Location) map.get("waitingLocation");
		}
		
		if (map.containsKey("leaveLocation")) {
			arena.leaveLocation = (Location) map.get("leaveLocation");
		}
		
		if (map.containsKey("limits")) {
			arena.limits = regionFromString((String)map.get("limits"));
		}
		
		if (map.containsKey("minNumOfPlayers")) {
			try {
				arena.minNumPlayers = (int) map.get("minNumOfPlayers");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				arena.minNumPlayers = 4;
			}
		}
		
		if (map.containsKey("maxNumOfPlayers")) {
			try {
				arena.maxNumPlayers = (int) map.get("maxNumOfPlayers");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				arena.maxNumPlayers = 40;
			}
		}
		
//		if (map.containsKey("arenaLogicID")) {
//			arena.setType(ArenasLogicsManager.loadArenaLogic(arena, arena.getID()));
//		}
		
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
		
		if (map.containsKey("regions")) {
			arena.setNamedRegionsFromStringsMap((Map<String, String>)map.get("regions"));
		}
		
		if (map.containsKey("isAvailable")) {
			if (map.get("isAvailable") != null && map.get("isAvailable") instanceof Boolean) {
				if ((Boolean)map.get("isAvailable")) {
					arena.state = GameState.LOADING;
					arena.setAvailable();
				}
			}
		}
		
		arena.initializeGUIHolder();
		
		return arena;
	}
	
	private Map<String, String> getNamedRegionsAsStrings() {
		return (this.regions.entrySet().stream().map((Entry<String, Region> entry) -> {
			return new AbstractMap.SimpleEntry<String, String>(entry.getKey(), regionToString(entry.getValue()));
		}).collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
	}
	
	private void setNamedRegionsFromStringsMap(Map<String, String> map) {
		this.regions = new HashMap<String, Region>();
		for (Entry<String, String> entry : map.entrySet()) {
			this.regions.put(entry.getKey(), regionFromString(entry.getValue()));
		}
	}
	
	public boolean isLoading() {
		return this.state == GameState.LOADING;
	}
	
	private void initializeGUIHolder() {
		this.guiHolder = new SelectionGUIHolder(36, "kits");
		int i = 0;
		for (Kit kit : this.kits) {
			System.out.println("Adding Icon!");
			this.guiHolder.addIcon(new KitIcon(kit));
			System.out.println("Finished Adding Icon!");
			i++;
		}
	}
	
	private void resetGUIHolder() {
		this.guiHolder.reset();
		int i = 0;
		for (Kit kit : this.kits) {
			this.guiHolder.addIcon(new KitIcon(kit));
			i++;
		}
	}
	
	private void givePlayerWaitingItems(Player player) {
		ItemStack kitsItemStack = new CustomItemStack(new ItemStack(Material.COMPASS)).setType("Kits Menu").getItemStack();
		ItemStack leaveItemStack = new CustomItemStack(new ItemStack(Material.BARRIER)).setType("Leave").getItemStack();
		player.getInventory().clear();
		player.getInventory().setItem(0, kitsItemStack);
		player.getInventory().setItem(8, leaveItemStack);
	}
	
	@Override
	public void openKits(Player player) {
		
		player.openInventory(this.guiHolder.getInventory(player));
		
	}
	
	@Override
	public void selectKitForPlayer(Kit kit, Player player) {
		
		this.currentPlayersKits.put(player.getUniqueId(), kit);
		
	}
	
	private void giveKitsToPlayers() {
		
		for (UUID uuid : getPlayers()) {
			
			Kit kit = this.currentPlayersKits.get(uuid);
			
			if (kit == null && !this.kits.isEmpty()) {
				
				kit = this.kits.get(0);
				
			}
			
			Player player = Bukkit.getPlayer(uuid);
			
			if (player == null) {
				continue;
			}
			
			player.getInventory().clear();
			
			if (kit != null) {
				
				kit.giveToPlayer(player);
				
			}
			
		}
		
	}
	
	private void savePlayersInventories() {
		
		for (UUID uuid : getPlayers()) {
			
			Player player = Bukkit.getPlayer(uuid);
			
			savePlayerInventory(player);
			
		}
		
	}
	
	private void loadPlayersInventories() {
		
		for (UUID uuid : getPlayers()) {
			
			Player player = Bukkit.getPlayer(uuid);
			
			loadPlayerInventory(player);
			
		}
		
	}
	
	private void savePlayerInventory(Player player) {
		
		this.playersInventories.put(player.getUniqueId(), player.getInventory().getStorageContents().clone());
		
	}
	
	private void loadPlayerInventory(Player player) {
		
		ItemStack[] inventory = this.playersInventories.get(player.getUniqueId());
		
		if (inventory == null) {
			return;
		}
		
		player.getInventory().setStorageContents(inventory);
		
		this.playersInventories.remove(player.getUniqueId());
		
	}
	
}
