package com.danieli1818.drminigames;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.danieli1818.drminigames.commands.ArenaCommands;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public final class DRMinigames extends JavaPlugin {
	
	private File arenasConfigFile;
	private FileConfiguration arenasConfig;

	@Override
	public void onEnable() {
		
		if (getWorldEditPlugin() == null) {
			System.err.println("plugin missing for loading: WorldEdit!");
			return;
		}
		
		createArenasConfigs();
		
		getCommand("drminigames").setExecutor(new ArenaCommands());
		
		System.out.println("plugin has successfully loaded!!!!");
		
	}
	
	@Override
	public void onDisable() {
		
		System.out.println("plugin has been disabled.");
		
	}
	
	private WorldEditPlugin getWorldEditPlugin() {
		return (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
	}
	
	private void createArenasConfigs() {
		
		AbstractMap.Entry<File, FileConfiguration> arenasConfigs = createConfigurationFile("arenas.yml");
		this.arenasConfigFile = arenasConfigs.getKey();
		this.arenasConfig = arenasConfigs.getValue();
		
	}
	
	private AbstractMap.Entry<File, FileConfiguration> createConfigurationFile(String name) {
		File configFile = new File(getDataFolder(), name);
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			saveResource(name, false);
		}
		
		FileConfiguration config = new YamlConfiguration();
		try {
			config.load(configFile);
			return new AbstractMap.SimpleEntry(configFile, config);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public FileConfiguration getArenasConfig() {
		return this.arenasConfig;
	}
	
}
