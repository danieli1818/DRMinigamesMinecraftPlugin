package com.danieli1818.drminigames.resources.api;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.danieli1818.drminigames.arena.kits.Kit;
import com.sk89q.worldedit.regions.Region;

public interface Arena extends ConfigurationSerializable {

	public List<UUID> getPlayers();
	
	public Map<String, Location> getSpawnLocation();
	
	public Location getWaitingLocation();
	
	public Location getLeaveLocation();
	
	public Region getLimits();
	
	public int getMinNumPlayers();
	
	public int getMaxNumPlayers();
	
	public ArenaLogic getAL();
	
	public long getCountdown();
	
	public void addKit(Kit kit);
	
	public void removeKit(Kit kit);
	
	public void removeAllKits();
	
	public boolean contains(UUID id);

	public boolean isAvailable();

	public boolean addPlayer(UUID uniqueId);

	public void setRegion(Region r);
	
	public boolean setRegion(Region r, String id);

	public boolean setUnavailable();

	public boolean setAvailable();

	public boolean setSpawnPoint(String name, Location location);

	public boolean setWaitingLocation(Location location);

	public boolean setLeavingLocation(Location location);

	public boolean setType(ArenaLogic arenaLogic);
	
	public boolean sendEvent(Event event);
	
	public Map<String, Region> getRegions();
	
	public boolean setMinNumOfPlayers(int num);
	
	public boolean setMaxNumOfPlayers(int num);
	
	public boolean stop();
	
	public void forceStop();
	
	public void sendCommand(Player player, String[] args);
	
	public String getID();
	
	public boolean isRunning();
	
	public void finishGame();
	
}
