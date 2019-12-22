package com.danieli1818.drminigames.utils;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.danieli1818.drminigames.arena.BaseArena;
import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.DRColorShooting;
import com.danieli1818.drminigames.common.BlockInformation;
import com.danieli1818.drminigames.common.configurationserializables.Timer;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;

public class SavingAndLoadingUtils {
	
	public static void saveMapIterative(Map<String, String> map, FileConfiguration conf, File file, String path) throws IOException {
		for (AbstractMap.Entry<String, String> entry : map.entrySet()) {
			conf.set(path + "." + entry.getKey(), entry.getValue());
		}
		if (file != null) {
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
	
	public static void saveMap(Map<String, Object> map, FileConfiguration conf, File file, String path) throws IOException {
		conf.createSection(path, map);
		if (file != null) {
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
	
	public static void saveSerializable(ConfigurationSerializable confSerializable, FileConfiguration conf, File file, String path) throws IOException {
		conf.set(path, confSerializable);
		if (file != null) {
			conf.save(file);
		}
	}
	
	public static void registerConfigurationSerializables() {
		ConfigurationSerialization.registerClass(Arena.class);
		ConfigurationSerialization.registerClass(BaseArena.class);
		ConfigurationSerialization.registerClass(ArenaLogic.class);
		ConfigurationSerialization.registerClass(DRColorShooting.class);
		ConfigurationSerialization.registerClass(Timer.class);
		ConfigurationSerialization.registerClass(BlockInformation.class);
		ConfigurationSerialization.registerClass(DRColorShooting.BlockPointsInformation.class);
	}
	
	public static void unregisterConfigurationSerializables() {
		ConfigurationSerialization.unregisterClass(Arena.class);
		ConfigurationSerialization.unregisterClass(BaseArena.class);
		ConfigurationSerialization.unregisterClass(ArenaLogic.class);
		ConfigurationSerialization.unregisterClass(DRColorShooting.class);
		ConfigurationSerialization.unregisterClass(Timer.class);
		ConfigurationSerialization.unregisterClass(BlockInformation.class);
		ConfigurationSerialization.unregisterClass(DRColorShooting.BlockPointsInformation.class);
	}
	
}
