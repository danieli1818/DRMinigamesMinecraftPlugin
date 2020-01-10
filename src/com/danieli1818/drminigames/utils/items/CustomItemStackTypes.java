package com.danieli1818.drminigames.utils.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.utils.ArenasManager;

import de.tr7zw.nbtapi.NBTItem;

public class CustomItemStackTypes {

	public static List<Action> getRightClickActions(ItemStack itemStack) {
		
		if (!CustomItemStack.isCustomItemStack(itemStack)) {
			return new ArrayList<Action>();
		}
		
		List<Action> actions = new ArrayList<Action>();
		
		NBTItem nbtItemStack = new NBTItem(itemStack);
		
		if (nbtItemStack.getString("Type") != null) {
			
			String type = nbtItemStack.getString("Type");
			
			if (type.equals("Kits Menu")) {
				
				actions.add(new Action() {
					
					@Override
					public void execute(Player player) {
						
						Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
						
						if (arena == null) {
							return;
						}
						
						arena.openKits(player);
						
					}
				});
				

				
			} else if (type.equals("Leave")) {
				
				actions.add(new Action() {
					
					@Override
					public void execute(Player player) {
						
						Arena arena = ArenasManager.getInstance().getArena(player.getUniqueId());
						
						if (arena == null) {
							return;
						}
						
						arena.removePlayer(player.getUniqueId());
						
					}
				});
				
				
				
			}
			
		}
		
		return actions;
		
	}
	
	public static List<Action> getLeftClickActions(ItemStack itemStack) {
		
		return new ArrayList<Action>();
		
	}
	
}
