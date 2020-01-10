package com.danieli1818.drminigames.utils.items;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemsListener implements Listener {

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.hasItem() && CustomItemStack.isCustomItemStack(event.getItem())) {
			if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
				onRightClickItem(event);
			} else {
				onLeftClickItem(event);
			}
		}
	}
	
	private void onRightClickItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		for (Action action : CustomItemStackTypes.getRightClickActions(event.getItem())) {
			action.execute(player);
		}
	}
	
	private void onLeftClickItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		for (Action action : CustomItemStackTypes.getLeftClickActions(event.getItem())) {
			action.execute(player);
		}
	}
	
}
