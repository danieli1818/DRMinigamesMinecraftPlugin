package com.danieli1818.drminigames.utils;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SavingAndLoadingUtils {
	
	public static void saveMapIterative(Map<String, String> map, FileConfiguration conf, File file, String path) throws IOException {
		for (AbstractMap.Entry<String, String> entry : map.entrySet()) {
			conf.set(path + "." + entry.getKey(), entry.getValue());
		}
		if (file == null) {
			conf.save(file);
		}
	}

	public static Map<String, String> loadMapIterative(FileConfiguration conf, File file, String path) {
		ConfigurationSection confSec = conf.getConfigurationSection(path);
		if (confSec == null) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		for (String key : confSec.getKeys(false)) {
			map.put(key, confSec.getString(key));
		}
		return map;
	}
	
	public static void saveMap(Map<String, String> map, FileConfiguration conf, File file, String path) throws IOException {
		conf.createSection(path, map);
		if (file == null) {
			conf.save(file);
		}
	}
	
	public static Map<String, String> loadMap(FileConfiguration conf, File file, String path) {
		ConfigurationSection confSec = conf.getConfigurationSection(path);
		if (confSec == null) {
			return null;
		}
		return confSec.getValues(false).entrySet().stream()
				.map(entry -> new AbstractMap.SimpleEntry<String, String>(entry.getKey(), (String)entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
}
