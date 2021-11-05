package com.danieli1818.drminigames;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.danieli1818.drminigames.arena.ArenasLogicsManager;
import com.danieli1818.drminigames.commands.ArenaCommands;
import com.danieli1818.drminigames.common.arenalogics.TeamsArenaLogic;
import com.danieli1818.drminigames.items.CustomItemsCreator;
import com.danieli1818.drminigames.listeners.MinigamesEventsListener;
import com.danieli1818.drminigames.utils.ArenasManager;
import com.danieli1818.drminigames.utils.SavingAndLoadingUtils;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public final class DRMinigames extends JavaPlugin {

	private File arenasConfigFile;
	private FileConfiguration arenasConfig;

	private File arenasLogicsConfigFile;
	private FileConfiguration arenasLogicsConfig;

	private File kitsConfigFile;
	private FileConfiguration kitsConfig;
	
	private final String arenasFileName = "arenas.yml";
	private final String arenasLogicsFileName = "arenasLogics.yml";

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
		
		createArenasLogicsFile();
		
		CustomItemsCreator.createCustomItems();

		afterAllPluginsWereLoaded(() -> {
			ArenasLogicsManager.getInstance().loadArenaLogicsFactoriesTypes();

			ArenasLogicsManager.getInstance().registerArenaLogicsFactoriesTypes();

			getServer().getScheduler().runTask(this, () -> {
				createArenasLogicsConfigs();
				
				ArenasManager.getInstance().loadArenasLogics();
			});

		});

		getCommand("drminigames").setExecutor(new ArenaCommands());

		getServer().getPluginManager().registerEvents(new MinigamesEventsListener(), this);

		ConfigurationSerialization.registerClass(TeamsArenaLogic.class);

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

		ArenasLogicsManager.getInstance().unregisterArenaLogicsFactoriesTypes();

		System.out.println("plugin has been disabled.");

	}

	private WorldEditPlugin getWorldEditPlugin() {
		return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	}

	private void createArenasConfigs() {

		AbstractMap.Entry<File, FileConfiguration> arenasConfigs = createConfigurationFile(arenasFileName);
		this.arenasConfigFile = arenasConfigs.getKey();
		this.arenasConfig = arenasConfigs.getValue();

	}

	private void createArenasLogicsConfigs() {

		AbstractMap.Entry<File, FileConfiguration> arenasLogicsConfigs = createConfigurationFile(arenasLogicsFileName);
		this.arenasLogicsConfigFile = arenasLogicsConfigs.getKey();
		this.arenasLogicsConfig = arenasLogicsConfigs.getValue();

	}
	
	private void createArenasLogicsFile() {
		this.arenasLogicsConfigFile = new File(getDataFolder(), arenasLogicsFileName);
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

	public void afterAllPluginsWereLoaded(Runnable runnable) {
		getServer().getScheduler().scheduleAsyncDelayedTask(this, runnable, 1);
	}

}
