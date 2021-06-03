package com.danieli1818.drminigames.utils.guis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.danieli1818.drminigames.utils.items.EffectsUtils;

public class SelectionGUIHolder implements InventoryHolder {
	
	private GUIHolder guiHolder;
	
	private Map<UUID, Integer> selectedSlot;

	public SelectionGUIHolder(int size, String title) {
		
		this.guiHolder = new GUIHolder(size, title);
		
		this.selectedSlot = new HashMap<UUID, Integer>();
		
	}
	
	public SelectionGUIHolder setIcon(int slot, Icon icon) {
		this.guiHolder.setIcon(slot, icon);
		return this;
	}
	
	public Icon getIcon(int slot) {
		return this.guiHolder.getIcon(slot);
	}
	
	public Map<Integer, Icon> getIcons() {
		return this.guiHolder.getIcons();
	}
	
	public SelectionGUIHolder clearIcons() {
		this.guiHolder.clearIcons();
		return this;
	}
	
	public boolean addIcon(Icon icon) {
		return this.guiHolder.addIcon(icon);
	}
	
	public boolean selectIcon(int slot, Player player) {
		Icon icon = this.getIcon(slot);
		if (icon == null) {
			return false;
		}
		this.selectedSlot.put(player.getUniqueId(), slot);
		runIconClickActions(icon, player);
		player.closeInventory();
		return true;
	}
	
	private boolean runIconClickActions(Icon icon, Player player) {
		
		if (icon == null) {
			return false;
		}
		
		for (ClickAction action : icon.getClickActions()) {
			if (action == null) {
				continue;
			}
			action.execute(player);
		}
		
		return true;
		
	}
	
	public void reset() {
		this.guiHolder.reset();
		this.selectedSlot.clear();
	}
	
	@Override
	public Inventory getInventory() {
		return this.guiHolder.getInventory(this);
	}
	
	public Inventory getInventory(Player player) {
		Inventory inventory = getInventory();
		if (this.selectedSlot.containsKey(player.getUniqueId())) {
			
			int slot = this.selectedSlot.get(player.getUniqueId());
			
			ItemStack itemStack = inventory.getItem(slot);
			
			itemStack = EffectsUtils.addGlow(itemStack);
			
			ItemMeta itemMeta = itemStack.getItemMeta();
			
			List<String> lore = new ArrayList<String>();
			
			if (itemMeta.getLore() != null) {
				lore.addAll(itemMeta.getLore());
			}
			
			lore.add("Selected Kit!");
			
			itemMeta.setLore(lore);
			
			itemStack.setItemMeta(itemMeta);
			
			inventory.setItem(slot, itemStack);
			
		}
		return inventory;
	}

	
	
}
