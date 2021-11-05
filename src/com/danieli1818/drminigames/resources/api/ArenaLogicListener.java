package com.danieli1818.drminigames.resources.api;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.danieli1818.drminigames.resources.api.arena.events.JoinEvent;


public interface ArenaLogicListener {

	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event);

	public void onPlayerAdvancementDoneEvent(PlayerAdvancementDoneEvent event);

	public void onPlayerAnimationEvent(PlayerAnimationEvent event);

	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event);

	public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent event);

	public void onPlayerBucketEvent(PlayerBucketEvent event);

	public void onPlayerChangedMainHandEvent(PlayerChangedMainHandEvent event);

	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event);

	public void onPlayerChannelEvent(PlayerChannelEvent event);

	public void onPlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent event);

	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event);

	public void onPlayerDropItemEvent(PlayerDropItemEvent event);

	public void onPlayerEditBookEvent(PlayerEditBookEvent event);

	public void onPlayerEggThrowEvent(PlayerEggThrowEvent event);

	public void onPlayerExpChangeEvent(PlayerExpChangeEvent event);

	public void onPlayerFishEvent(PlayerFishEvent event);

	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event);

	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event);

	public void onPlayerInteractEvent(PlayerInteractEvent event);

	public void onPlayerItemBreakEvent(PlayerItemBreakEvent event);

	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event);

	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event);

	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event);

	public void onPlayerItemMendEvent(PlayerItemMendEvent event);

	public void onPlayerJoinEvent(PlayerJoinEvent event);

	public void onPlayerKickEvent(PlayerKickEvent event);

	public void onPlayerLevelChangeEvent(PlayerLevelChangeEvent event);

	public void onPlayerLocaleChangeEvent(PlayerLocaleChangeEvent event);

	public void onPlayerLoginEvent(PlayerLoginEvent event);

	public void onPlayerMoveEvent(PlayerMoveEvent event);

	public void onPlayerQuitEvent(PlayerQuitEvent event);

	public void onPlayerResourcePackStatusEvent(PlayerResourcePackStatusEvent event);

	public void onPlayerRespawnEvent(PlayerRespawnEvent event);

	public void onPlayerShearEntityEvent(PlayerShearEntityEvent event);

	public void onPlayerStatisticIncrementEvent(PlayerStatisticIncrementEvent event);

	public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event);

	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent event);

	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event);

	public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent event);

	public void onPlayerVelocityEvent(PlayerVelocityEvent event);

	public void onProjectileHitEvent(ProjectileHitEvent event);
	
	public void onJoinGameEvent(JoinEvent event);
	
	public void onPlayerDeathEvent(PlayerDeathEvent event);
	
}
