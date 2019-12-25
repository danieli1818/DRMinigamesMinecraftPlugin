package com.danieli1818.drminigames.utils;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.arena.ArenasLogicsManager;
import com.danieli1818.drminigames.arena.BaseArena;
import com.danieli1818.drminigames.resources.api.Arena;

public class ArenasManager {

	private static ArenasManager am;

	private Map<String, Arena> arenas;

	private static DRMinigames plugin = DRMinigames.getPlugin(DRMinigames.class);

	private static FileConfiguration config = plugin.getConfig();

	private static FileConfiguration arenasConfig = plugin.getArenasConfig();

	private static File arenasConfigFile = plugin.getArenasConfigFile();

	private ArenasManager() {
		this.arenas = new HashMap<String, Arena>();
	}

	public static ArenasManager getInstance() {
		if (am == null) {
			return am = new ArenasManager();
		}
		return am;

	}

	public void reloadArenas() {

		this.arenas.clear();

		// Plugin plugin = DRMinigames.getPlugin(DRMinigames.class);

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
			Arena arena = loadArenaByID(configArena);
			this.arenas.put(configArena, arena);
		}
	}

	public Arena getArena(String id) {
		return this.arenas.get(id);
	}

	public boolean doesExist(String id) {

		if (!this.config.contains("arenas")) {
			return false;
		}

		List<String> arenas = this.config.getStringList("arenas");

		return arenas.contains(id);

	}

	public Arena getArena(UUID id) {
		for (Arena arena : this.arenas.values()) {
			if (arena.contains(id)) {
				return arena;
			}
		}
		return null;
	}

	public boolean addArena(String id) {

		if (!this.config.contains("arenas")) {
			this.config.set("arenas", this.arenas);
		}

		reloadArenas();

		if (this.arenas.containsKey(id)) {
			return false;
		}

		this.arenas.put(id, new BaseArena(id));

		// List<String> arenas = this.config.getStringList("arenas");
		//
		// if (arenas.contains(id)) {
		// return false;
		// }
		//
		// arenas.add(id);

		List<String> arenas = new ArrayList<String>();

		arenas.addAll(this.arenas.keySet());

		this.config.set("arenas", arenas);

		// this.config.set(id, null);

		this.plugin.saveConfig();

		return true;

	}

	public List<String> getArenasIDs() {

		return this.config.getStringList("arenas");

	}

	private Arena loadArenaByID(String id) {
		if (!this.arenasConfig.contains(id)) {
			return new BaseArena(id);
		}
		Arena arena = (Arena) arenasConfig.get(id);
		this.arenas.put(id, arena);
		return arena;
	}

	public void saveArenas() throws IOException {
		for (AbstractMap.Entry<String, Arena> entry : this.arenas.entrySet()) {
			try {
				SavingAndLoadingUtils.saveSerializable(entry.getValue(), arenasConfig, null, entry.getKey());
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		arenasConfig.save(arenasConfigFile);
	}

	public void saveArenas(String[] ids) throws IOException {
		for (String id : ids) {
			if (this.doesExist(id)) {
				try {
					SavingAndLoadingUtils.saveSerializable(this.getArena(id), arenasConfig, null, id);
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
		arenasConfig.save(arenasConfigFile);
	}

	public void loadArenasLogics() {
		for (Arena arena : this.arenas.values()) {
			arena.setType(ArenasLogicsManager.loadArenaLogic(arena));
		}
	}

}
