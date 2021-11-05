package com.danieli1818.drminigames.arena;

import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.resources.api.ArenaLogicListener;
import com.danieli1818.drminigames.resources.api.arena.events.JoinEvent;

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



public abstract class BaseArenaListener implements ArenaLogicListener {

	public abstract ArenaLogic getArenaLogic();
	
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		getArenaLogic().getListener().onAsyncPlayerChatEvent(event);
	}

	public void onPlayerAdvancementDoneEvent(PlayerAdvancementDoneEvent event) {
		getArenaLogic().getListener().onPlayerAdvancementDoneEvent(event);
	}

	public void onPlayerAnimationEvent(PlayerAnimationEvent event) {
		getArenaLogic().getListener().onPlayerAnimationEvent(event);
	}

	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
		getArenaLogic().getListener().onPlayerBedEnterEvent(event);
	}

	public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent event) {
		getArenaLogic().getListener().onPlayerBedLeaveEvent(event);
	}

	public void onPlayerBucketEvent(PlayerBucketEvent event) {
		getArenaLogic().getListener().onPlayerBucketEvent(event);
	}

	public void onPlayerChangedMainHandEvent(PlayerChangedMainHandEvent event) {
		getArenaLogic().getListener().onPlayerChangedMainHandEvent(event);
	}

	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
		getArenaLogic().getListener().onPlayerChangedWorldEvent(event);
	}

	public void onPlayerChannelEvent(PlayerChannelEvent event) {
		getArenaLogic().getListener().onPlayerChannelEvent(event);
	}

	public void onPlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent event) {
		getArenaLogic().getListener().onPlayerChatTabCompleteEvent(event);
	}

	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		getArenaLogic().getListener().onPlayerCommandPreprocessEvent(event);
	}

	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		getArenaLogic().getListener().onPlayerDropItemEvent(event);
	}

	public void onPlayerEditBookEvent(PlayerEditBookEvent event) {
		getArenaLogic().getListener().onPlayerEditBookEvent(event);
	}

	public void onPlayerEggThrowEvent(PlayerEggThrowEvent event) {
		getArenaLogic().getListener().onPlayerEggThrowEvent(event);
	}

	public void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
		getArenaLogic().getListener().onPlayerExpChangeEvent(event);
	}

	public void onPlayerFishEvent(PlayerFishEvent event) {
		getArenaLogic().getListener().onPlayerFishEvent(event);
	}

	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		getArenaLogic().getListener().onPlayerGameModeChangeEvent(event);
	}

	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		getArenaLogic().getListener().onPlayerInteractEntityEvent(event);
	}

	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		getArenaLogic().getListener().onPlayerInteractEvent(event);
	}

	public void onPlayerItemBreakEvent(PlayerItemBreakEvent event) {
		getArenaLogic().getListener().onPlayerItemBreakEvent(event);
	}

	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		getArenaLogic().getListener().onPlayerItemConsumeEvent(event);
	}

	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
		getArenaLogic().getListener().onPlayerItemDamageEvent(event);
	}

	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
		getArenaLogic().getListener().onPlayerItemHeldEvent(event);
	}

	public void onPlayerItemMendEvent(PlayerItemMendEvent event) {
		getArenaLogic().getListener().onPlayerItemMendEvent(event);
	}

	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		getArenaLogic().getListener().onPlayerJoinEvent(event);
	}

	public void onPlayerKickEvent(PlayerKickEvent event) {
		getArenaLogic().getListener().onPlayerKickEvent(event);
	}

	public void onPlayerLevelChangeEvent(PlayerLevelChangeEvent event) {
		getArenaLogic().getListener().onPlayerLevelChangeEvent(event);
	}

	public void onPlayerLocaleChangeEvent(PlayerLocaleChangeEvent event) {
		getArenaLogic().getListener().onPlayerLocaleChangeEvent(event);
	}

	public void onPlayerLoginEvent(PlayerLoginEvent event) {
		getArenaLogic().getListener().onPlayerLoginEvent(event);
	}

	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		getArenaLogic().getListener().onPlayerMoveEvent(event);
	}

	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		getArenaLogic().getListener().onPlayerQuitEvent(event);
	}

	public void onPlayerResourcePackStatusEvent(PlayerResourcePackStatusEvent event) {
		getArenaLogic().getListener().onPlayerResourcePackStatusEvent(event);
	}

	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		getArenaLogic().getListener().onPlayerRespawnEvent(event);
	}

	public void onPlayerShearEntityEvent(PlayerShearEntityEvent event) {
		getArenaLogic().getListener().onPlayerShearEntityEvent(event);
	}

	public void onPlayerStatisticIncrementEvent(PlayerStatisticIncrementEvent event) {
		getArenaLogic().getListener().onPlayerStatisticIncrementEvent(event);
	}

	public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
		getArenaLogic().getListener().onPlayerSwapHandItemsEvent(event);
	}

	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent event) {
		getArenaLogic().getListener().onPlayerToggleFlightEvent(event);
	}

	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
		getArenaLogic().getListener().onPlayerToggleSneakEvent(event);
	}

	public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent event) {
		getArenaLogic().getListener().onPlayerToggleSprintEvent(event);
	}

	public void onPlayerVelocityEvent(PlayerVelocityEvent event) {
		getArenaLogic().getListener().onPlayerVelocityEvent(event);
	}

	@Override
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		getArenaLogic().getListener().onProjectileHitEvent(event);
	}
	
	@Override
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		getArenaLogic().getListener().onPlayerDeathEvent(event);
	}
	
	@Override
	public void onJoinGameEvent(JoinEvent event) {
		getArenaLogic().getListener().onJoinGameEvent(event);
	}
	
}
