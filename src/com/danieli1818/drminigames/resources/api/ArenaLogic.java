package com.danieli1818.drminigames.resources.api;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface ArenaLogic extends Observer, ConfigurationSerializable {

	public void start(Arena arena);
	
//	public void onPlayerJoined(Player p);
	
	public boolean canBeAvailable(Arena arena);
	
	@Override
	default void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		System.out.println("Updated ArenaLogic!");
	}
	
	public boolean stop();
	
	public void forceStop();
	
	public boolean command(Player player, String[] args);
	
}
