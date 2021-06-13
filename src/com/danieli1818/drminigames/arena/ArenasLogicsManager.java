package com.danieli1818.drminigames.arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.resources.api.ArenaLogicFactory;
import com.danieli1818.drminigames.resources.api.DRMinigamePlugin;
import com.danieli1818.drminigames.utils.SavingAndLoadingUtils;

public class ArenasLogicsManager {
	
	private static ArenasLogicsManager instance;
	
	private static DRMinigames plugin = DRMinigames.getPlugin(DRMinigames.class);
	
	private static FileConfiguration arenasLogicsConfig = plugin.getArenasLogicsConfig();
	
	private static File arenasLogicsConfigFile = plugin.getArenasLogicsConfigFile();
	
	private Map<String, ArenaLogicFactory> arenaLogicsFactoriesTypes;
	
	private ArenasLogicsManager() {
		arenaLogicsFactoriesTypes = new HashMap<>();
	}
	
	public static ArenasLogicsManager getInstance() {
		if (instance == null) {
			instance = new ArenasLogicsManager();
		}
		return instance;
	}

	public static ArenaLogic loadArenaLogic(Arena arena) {
		if (arenasLogicsConfig == null) {
			return null;
		}
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
	
	public void loadArenaLogicsFactoriesTypes() {
		for (DRMinigamePlugin drMinigamePlugin : getDRMinigamePlugins()) {
			System.out.println("Successfully loading " + drMinigamePlugin.getID().toUpperCase());
			this.arenaLogicsFactoriesTypes.put(drMinigamePlugin.getID().toUpperCase(), drMinigamePlugin.getArenaLogicFactory());
			System.out.println("Successfully finished loading " + drMinigamePlugin.getID().toUpperCase());
		}
	}
	
	public void registerArenaLogicsFactoriesTypes() {
		for (DRMinigamePlugin drMinigamePlugin : getDRMinigamePlugins()) {
			drMinigamePlugin.registerSerializableClasses();
		}
	}
	
	public void unregisterArenaLogicsFactoriesTypes() {
		for (DRMinigamePlugin drMinigamePlugin : getDRMinigamePlugins()) {
			drMinigamePlugin.unregisterSerializableClasses();
		}
	}
	
	private DRMinigamePlugin[] getDRMinigamePlugins() {
		Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
		List<DRMinigamePlugin> drMinigamePlugins = new ArrayList<>();
		for (Plugin plugin : plugins) {
			System.out.println(plugin.getName());
			if (plugin instanceof DRMinigamePlugin) {
				System.out.println("yay enabled and identified!");
				DRMinigamePlugin drMinigamePlugin = (DRMinigamePlugin)plugin;
				drMinigamePlugins.add(drMinigamePlugin);
			}
		}
		return drMinigamePlugins.toArray(new DRMinigamePlugin[drMinigamePlugins.size()]);
	}
	
	public Set<String> getArenaLogicsTypes() {
		return this.arenaLogicsFactoriesTypes.keySet();
	}
	
	public ArenaLogicFactory getArenaLogicFactory(String arenaLogicType) {
		return this.arenaLogicsFactoriesTypes.get(arenaLogicType);
	}
	
}
