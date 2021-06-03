package com.danieli1818.drminigames.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.utils.ArenasManager;

public class MinigamesEventsListener implements Listener {
	
	public MinigamesEventsListener() {}

	@EventHandler
	public void onBlockProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile == null) {
			return;
		}
		ProjectileSource shooter = projectile.getShooter();
		if (shooter != null && shooter instanceof Player) {
			Player player = (Player)shooter;
			Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
			if (arena != null) {
				arena.sendEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.sendEvent(event);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.sendEvent(event);
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.sendEvent(event);
		}
	}
	
}
