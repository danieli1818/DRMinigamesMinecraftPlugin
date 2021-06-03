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
import com.danieli1818.drminigames.listeners.MinigamesEventsListener;
import com.danieli1818.drminigames.utils.ArenasManager;
import com.danieli1818.drminigames.utils.SavingAndLoadingUtils;
import com.danieli1818.drminigames.utils.guis.GUIListener;
import com.danieli1818.drminigames.utils.items.ItemsListener;
import com.danieli1818.drminigames.utils.items.enchantments.EnchantmentsManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public final class DRMinigames extends JavaPlugin {
	
	private File arenasConfigFile;
	private FileConfiguration arenasConfig;
	
	private File arenasLogicsConfigFile;
	private FileConfiguration arenasLogicsConfig;
	
	private File kitsConfigFile;
	private FileConfiguration kitsConfig;

	@Override
	public void onEnable() {
		
		if (getWorldEditPlugin() == null) {
			System.err.println("plugin missing for loading: WorldEdit!");
			return;
		}
		
		SavingAndLoadingUtils.registerConfigurationSerializables();
		
		createKitsConfigs();
		
		createArenasConfigs();
		
		ArenasManager.getInstance().reloadArenas();
		
		createArenasLogicsConfigs();
		
		ArenasManager.getInstance().loadArenasLogics();
		
		getCommand("drminigames").setExecutor(new ArenaCommands());
		
		getServer().getPluginManager().registerEvents(new MinigamesEventsListener(), this);
		
		getServer().getPluginManager().registerEvents(new GUIListener(), this);
		
		getServer().getPluginManager().registerEvents(new ItemsListener(), this);
		
		EnchantmentsManager.registerAllEnchantments();
		
		System.out.println("plugin has successfully loaded!!!!");
		
	}
	
	@Override
	public void onDisable() {
		
		try {
			ArenasManager.getInstance().saveArenas();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SavingAndLoadingUtils.unregisterConfigurationSerializables();
		
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
	
	private void createArenasLogicsConfigs() {
		
		AbstractMap.Entry<File, FileConfiguration> arenasLogicsConfigs = createConfigurationFile("arenasLogics.yml");
		this.arenasLogicsConfigFile = arenasLogicsConfigs.getKey();
		this.arenasLogicsConfig = arenasLogicsConfigs.getValue();
		
	}
	
	private void createKitsConfigs() {
		
		AbstractMap.Entry<File, FileConfiguration> kitsConfigs = createConfigurationFile("kits.yml");
		this.kitsConfigFile = kitsConfigs.getKey();
		this.kitsConfig = kitsConfigs.getValue();
		
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
	
	public File getArenasConfigFile() {
		return this.arenasConfigFile;
	}
	
	public FileConfiguration getArenasLogicsConfig() {
		return this.arenasLogicsConfig;
	}
	
	public File getArenasLogicsConfigFile() {
		return this.arenasLogicsConfigFile;
	}
	
	public FileConfiguration getKitsConfig() {
		return this.kitsConfig;
	}
	
	public File getKitsConfigFile() {
		return this.kitsConfigFile;
	}
	
}
