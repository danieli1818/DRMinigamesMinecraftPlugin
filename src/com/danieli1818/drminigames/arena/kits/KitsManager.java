package com.danieli1818.drminigames.arena.kits;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import com.danieli1818.drminigames.DRMinigames;

public class KitsManager {
	
	private Map<String, Kit> kits;
	
	private static final DRMinigames plugin = DRMinigames.getPlugin(DRMinigames.class);
	
	private static File configurationFile = plugin.getKitsConfigFile();
	
	private static FileConfiguration configuration = plugin.getKitsConfig();
	
	private static KitsManager instance;
	
	public static KitsManager getInstance() {
		
		if (instance == null) {
			instance = new KitsManager();
		}
		return instance;
		
	}
	
	private KitsManager() {
		this.kits = new HashMap<String, Kit>();
	}
	
	public Kit loadKit(String id) {
		if (this.kits.containsKey(id)) {
			return this.kits.get(id);
		}
		if (KitsManager.configuration.contains(id)) {
			Kit kit = KitsManager.configuration.getSerializable(id, Kit.class);
			this.kits.put(id, kit);
			return kit;
		}
		return null;
	}
	
	public boolean saveKit(String id, Kit kit) {
		this.kits.put(id, kit);
		this.configuration.set(id, kit);
		try {
			this.configuration.save(this.configurationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean saveAllKits() {
		for (Entry<String, Kit> entry : this.kits.entrySet()) {
			this.configuration.set(entry.getKey(), entry.getValue());
		}
		try {
			this.configuration.save(this.configurationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void loadAllKits() {
		for (String key : this.configuration.getKeys(false)) {
			loadKit(key);
		}
	}

}
