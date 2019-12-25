package com.danieli1818.drminigames.arena;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

	public static ArenaLogic loadArenaLogic(Arena arena) {
		ArenaLogic al = arenasLogicsConfig.getSerializable(arena.getID(), ArenaLogic.class);
		return al;
	}
	
	public static void saveArenaLogic(ArenaLogic al, String id) throws IOException {
		SavingAndLoadingUtils.saveSerializable(al, arenasLogicsConfig, arenasLogicsConfigFile, id);
	}
	
	public void loadArenasLogics(List<Arena> arenas) {
		for (Arena arena : arenas) {
			if (arena != null) {
				arena.setType(loadArenaLogic(arena));
			}
		}
	}
	
}
