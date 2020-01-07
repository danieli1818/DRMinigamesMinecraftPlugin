package com.danieli1818.drminigames.arena.kits;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface Kit extends ConfigurationSerializable {

	void giveToPlayer(Player player);
	
	String getID();
	
}
