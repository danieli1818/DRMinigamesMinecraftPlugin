package com.danieli1818.drminigames.resources.api;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface ArenaLogic extends ConfigurationSerializable {

	public void start(Arena arena);
	
//	public void onPlayerJoined(Player p);
	
	public String canBeAvailable(Arena arena);
	
	public boolean stop();
	
	public void forceStop();
	
	public boolean command(Player player, String[] args);
	
	public ArenaLogicListener getListener();
	
	public void removePlayer(Player player);
	
}
