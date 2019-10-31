package com.danieli1818.drminigames.resources.api;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import org.bukkit.Location;

import com.danieli1818.drminigames.arena.kits.Kit;
import com.sk89q.worldedit.regions.Region;

public interface Arena {

	public List<UUID> getPlayers();
	
	public Map<String, Location> getSpawnLocation();
	
	public Location getWaitingLocation();
	
	public Location getLeaveLocation();
	
	public Region getLimits();
	
	public int getMinNumPlayers();
	
	public int getMaxNumPlayers();
	
	public ArenaLogic getAL();
	
	public Timer getTimer();
	
	public long getCountdown();
	
	public void addKit(Kit kit);
	
	public void removeKit(Kit kit);
	
	public void removeAllKits();
	
}
