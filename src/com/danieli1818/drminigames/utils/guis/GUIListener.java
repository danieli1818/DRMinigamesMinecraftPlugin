package com.danieli1818.drminigames.utils.guis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		if (event.getView().getTopInventory().getHolder() instanceof GUIHolder) {
			
			event.setCancelled(true);
			
			if (event.getWhoClicked() instanceof Player) {
				
				GUIHolder gui = (GUIHolder)event.getView().getTopInventory().getHolder();
				
				Player player = (Player)event.getWhoClicked();
				
				Icon icon = gui.getIcon(event.getRawSlot());
				
				if (icon == null) {
					return;
				}
				
				for (ClickAction clickAction : icon.getClickActions()) {
					clickAction.execute(player);
				}
				
			}
			
		} else if (event.getView().getTopInventory().getHolder() instanceof SelectionGUIHolder) {
			
			event.setCancelled(true);
			
			if (event.getWhoClicked() instanceof Player) {
				
				SelectionGUIHolder gui = (SelectionGUIHolder)event.getView().getTopInventory().getHolder();
				
				Player player = (Player)event.getWhoClicked();
				
				gui.selectIcon(event.getRawSlot(), player);
				
			}
			
		}
		
	}
	
}
