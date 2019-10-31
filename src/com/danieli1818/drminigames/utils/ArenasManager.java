package com.danieli1818.drminigames.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.arena.BaseArena;

public class ArenasManager {
	
	private static ArenasManager am;
	
	private Map<String, BaseArena> arenas;
	
	private static Plugin plugin = DRMinigames.getPlugin(DRMinigames.class);
		
	private static FileConfiguration config = plugin.getConfig();

	private ArenasManager() {
		this.arenas = new HashMap<String, BaseArena>();
	}
	
	public static ArenasManager getInstance() {
		if (am == null) {
			return am = new ArenasManager();
		}
		return am;
		
	}
	
	public void reloadArenas() {
		
		this.arenas.clear();
		
//		Plugin plugin = DRMinigames.getPlugin(DRMinigames.class);
		
		List<String> configArenas = this.config.getStringList("arenas");
		
		if (configArenas == null) {
			System.err.println("Error No Arenas Detected In Config.YML!!!!");
			return;
		}
		
		for (String configArena : configArenas) {
			if (!doesExist(configArena)) {
				System.err.println("Arena " + configArena + " does not exist!");
				continue;
			}
			BaseArena arena = new BaseArena(configArena);
			this.arenas.put(configArena, arena);
		}
	}
	
	public BaseArena getArena(String id) {
		return this.arenas.get(id);
	}
	
	public boolean doesExist(String id) {
				
		if (!this.config.contains("arenas")) {
			return false;
		}
		
		List<String> arenas = this.config.getStringList("arenas");
		
		return arenas.contains(id);
		
	}
	
	public BaseArena getArena(UUID id) {
		for (BaseArena arena : this.arenas.values()) {
			if (arena.contains(id)) {
				return arena;
			}
		}
		return null;
	}
	
	public boolean addArena(String id) {
		
		if (this.config.contains(id)) {
			return false;
		}
		
		this.arenas.put(id, new BaseArena(id));
		
		List<String> arenas = this.config.getStringList("arenas");
		
		if (arenas.contains(id)) {
			return false;
		}
		
		arenas.add(id);
		
		this.config.set("arenas", arenas);
		
		this.config.set(id, null);
		
		this.plugin.saveConfig();
		
		return true;
		
	}
	
	public List<String> getArenasIDs() {
		
		return this.config.getStringList("arenas");
		
	}
	
	
}
