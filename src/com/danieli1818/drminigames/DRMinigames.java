package com.danieli1818.drminigames;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.danieli1818.drminigames.commands.ArenaCommands;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public final class DRMinigames extends JavaPlugin {

	@Override
	public void onEnable() {
		
		if (getWorldEditPlugin() == null) {
			System.err.println("plugin missing for loading: WorldEdit!");
			return;
		}
		
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
	
}
