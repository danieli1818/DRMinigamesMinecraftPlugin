package com.danieli1818.drminigames.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.utils.ArenasManager;

import drcustomitems.items.CustomItemsBuilder;
import drcustomitems.items.actions.Action;
import drcustomitems.items.actions.ActionsHolder;
import drcustomitems.items.actions.BaseActionsHolder;
import drcustomitems.items.actions.CustomItemsActionsManager;

public class CustomItemsCreator {

	public static void createCustomItems() {
		createKitsMenuItem();
		createLeaveItem();
	}
	
	private static ItemStack createKitsMenuItem() {
		ItemStack kitsMenuCustomItemStack = new CustomItemsBuilder(Material.COMPASS).
												setDisplayName("Kits Menu").
												setLore(new String[] {"Click for the kits menu!"}).
												create("kits_menu", DRMinigames.getPlugin(DRMinigames.class).getName());
		ActionsHolder kitsMenuActionsHolder = new BaseActionsHolder().addClickAction(new Action() {
			
			@Override
			public void runAction(Player player) {
				ArenasManager.getInstance().getArena(player.getUniqueId()).openKits(player);
				
			}
		});
		CustomItemsActionsManager.getInstance().addActionsHolderToItemStack(kitsMenuCustomItemStack, kitsMenuActionsHolder);
		return kitsMenuCustomItemStack;
	}
	
	private static ItemStack createLeaveItem() {
		ItemStack leaveCustomItemStack = new CustomItemsBuilder(Material.BARRIER).
																setDisplayName("Leave Game").
																setLore(new String[] {"Click to leave game!"}).
																create("leave_game", DRMinigames.getPlugin(DRMinigames.class).getName());
		ActionsHolder leaveActionsHolder = new BaseActionsHolder().addClickAction(new Action() {
			
			@Override
			public void runAction(Player player) {
				ArenasManager.getInstance().getArena(player.getUniqueId()).removePlayer(player.getUniqueId());
				
			}
		}).setShouldCancelClick(true);
		CustomItemsActionsManager.getInstance().addActionsHolderToItemStack(leaveCustomItemStack, leaveActionsHolder);
		return leaveCustomItemStack;
	}
	
}
