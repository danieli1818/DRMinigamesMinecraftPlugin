package com.danieli1818.drminigames.arena;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.DRColorShooting;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.SavingAndLoadingUtils;

public class ArenasLogicsManager {
	
	private static ArenasLogicsManager instance;
	
	private static DRMinigames plugin = DRMinigames.getPlugin(DRMinigames.class);
	
	private static FileConfiguration arenasLogicsConfig = plugin.getArenasLogicsConfig();
	
	private static File arenasLogicsConfigFile = plugin.getArenasLogicsConfigFile();
	
	private ArenasLogicsManager() {
		
	}
	
	public static ArenasLogicsManager getInstance() {
		if (instance == null) {
			instance = new ArenasLogicsManager();
		}
		return instance;
	}

	public static ArenaLogic loadArenaLogic(Arena arena, String id) {
		System.out.println("Loading ArenaLogic!");
		ArenaLogic al = arenasLogicsConfig.getSerializable(id, ArenaLogic.class);
//		Map<String, String> arenaLogicMap = (Map<String, String>) arenasLogicsConfig.get(id);
//		if (arenaLogicMap == null || arenaLogicMap.isEmpty() || !arenaLogicMap.containsKey("id")) {
//			return null;
//		}
//		String gameID = arenaLogicMap.get("id");
//		if (gameID == null) {
//			return null;
//		}
//		if (gameID.equals("DRColorShooting")) {
//			ArenaLogic al = new DRColorShooting(arena);
//			al.loadArenaLogicFromMap(arenaLogicMap);
//			return al;
//		}
		return al;
	}
	
	public static void saveArenaLogic(ArenaLogic al, String id) throws IOException {
		SavingAndLoadingUtils.saveSerializable(al, arenasLogicsConfig, arenasLogicsConfigFile, id);
	}
	
}
