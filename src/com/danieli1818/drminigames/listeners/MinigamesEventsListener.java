package com.danieli1818.drminigames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;


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
				arena.onProjectileHitEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerDropItemEvent(event);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerQuitEvent(event);
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerKickEvent(event);
		}
	}
	
	@EventHandler
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onAsyncPlayerChatEvent(event);
		}
	}

	@EventHandler
	public void onPlayerAdvancementDoneEvent(PlayerAdvancementDoneEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerAdvancementDoneEvent(event);
		}
	}

	@EventHandler
	public void onPlayerAnimationEvent(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerAnimationEvent(event);
		}
	}

	@EventHandler
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerBedEnterEvent(event);
		}
	}

	@EventHandler
	public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerBedLeaveEvent(event);
		}
	}

	@EventHandler
	public void onPlayerChangedMainHandEvent(PlayerChangedMainHandEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerChangedMainHandEvent(event);
		}
	}

	@EventHandler
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerChangedWorldEvent(event);
		}
	}

	@EventHandler
	public void onPlayerChannelEvent(PlayerChannelEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerChannelEvent(event);
		}
	}

	@EventHandler
	public void onPlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerChatTabCompleteEvent(event);
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerCommandPreprocessEvent(event);
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerDropItemEvent(event);
		}
	}

	@EventHandler
	public void onPlayerEditBookEvent(PlayerEditBookEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerEditBookEvent(event);
		}
	}

	@EventHandler
	public void onPlayerEggThrowEvent(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerEggThrowEvent(event);
		}
	}

	@EventHandler
	public void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerExpChangeEvent(event);
		}
	}

	@EventHandler
	public void onPlayerFishEvent(PlayerFishEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerFishEvent(event);
		}
	}

	@EventHandler
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerGameModeChangeEvent(event);
		}
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerInteractEntityEvent(event);
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerInteractEvent(event);
		}
	}

	@EventHandler
	public void onPlayerItemBreakEvent(PlayerItemBreakEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerItemBreakEvent(event);
		}
	}

	@EventHandler
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerItemConsumeEvent(event);
		}
	}

	@EventHandler
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerItemDamageEvent(event);
		}
	}

	@EventHandler
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerItemHeldEvent(event);
		}
	}

	@EventHandler
	public void onPlayerItemMendEvent(PlayerItemMendEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerItemMendEvent(event);
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerJoinEvent(event);
		}
	}

	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerKickEvent(event);
		}
	}

	@EventHandler
	public void onPlayerLevelChangeEvent(PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerLevelChangeEvent(event);
		}
	}

	@EventHandler
	public void onPlayerLocaleChangeEvent(PlayerLocaleChangeEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerLocaleChangeEvent(event);
		}
	}

	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerLoginEvent(event);
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerMoveEvent(event);
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerQuitEvent(event);
		}
	}

	@EventHandler
	public void onPlayerResourcePackStatusEvent(PlayerResourcePackStatusEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerResourcePackStatusEvent(event);
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerRespawnEvent(event);
		}
	}

	@EventHandler
	public void onPlayerShearEntityEvent(PlayerShearEntityEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerShearEntityEvent(event);
		}
	}

	@EventHandler
	public void onPlayerStatisticIncrementEvent(PlayerStatisticIncrementEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerStatisticIncrementEvent(event);
		}
	}

	@EventHandler
	public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerSwapHandItemsEvent(event);
		}
	}

	@EventHandler
	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerToggleFlightEvent(event);
		}
	}

	@EventHandler
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerToggleSneakEvent(event);
		}
	}

	@EventHandler
	public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerToggleSprintEvent(event);
		}
	}

	@EventHandler
	public void onPlayerVelocityEvent(PlayerVelocityEvent event) {
		Player player = event.getPlayer();
		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
		if (arena != null) {
			arena.onPlayerVelocityEvent(event);
		}
	}




//	@EventHandler
//	public void onPlayerEvent(PlayerEvent event) {
//		System.out.println("Event: " + event.toString());
//		Player player = event.getPlayer();
//		Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
//		if (arena != null) {
//			arena.sendEvent(event);
//		}
//	}
	
}
