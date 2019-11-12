package com.danieli1818.drminigames.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.utils.ArenasManager;

public class MinigamesEventsListener implements Listener {

	@EventHandler
	public void onBlockProjectileHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();
		if (entity != null && entity instanceof Player) {
			Player player = (Player)entity;
			Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		}
	}
	
}
