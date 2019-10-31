package com.danieli1818.drminigames.resources.api.arena.events;

import org.bukkit.entity.Player;

public class JoinEvent implements Event {
	
	private Player p;
	
	public JoinEvent(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return this.p;
	}
	
}
