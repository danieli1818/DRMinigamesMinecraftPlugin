package com.danieli1818.drminigames.arena;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.resources.api.ArenaLogic;

public class ArenasLogicsManager {
	
	private static ArenasLogicsManager instance;
	
	private static DRMinigames plugin = DRMinigames.getPlugin(DRMinigames.class);
	
	private static FileConfiguration arenasLogicsConfig = plugin.getArenasLogicsConfig();
	
	private ArenasLogicsManager() {
		
	}
	
	public static ArenasLogicsManager getInstance() {
		if (instance == null) {
			instance = new ArenasLogicsManager();
		}
		return instance;
	}

	public static ArenaLogic loadArenaLogic(String id) {
		return null;
	}
	
	public static void saveArenaLogic(ArenaLogic al) {
		
	}
	
}
