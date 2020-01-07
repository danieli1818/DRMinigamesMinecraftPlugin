package com.danieli1818.drminigames.utils.guis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {
	
	private final Map<Integer, Icon> icons;

	private final int size;
	private final String title;
	
	public GUIHolder(int size, String title) {
		this.icons = new HashMap<Integer, Icon>();
		this.size = size;
		this.title = title;
	}
	
	public GUIHolder setIcon(int slot, Icon icon) {
		this.icons.put(slot, icon);
		return this;
	}
	
	public Icon getIcon(int slot) {
		return this.icons.get(slot);
	}
	
	public Map<Integer, Icon> getIcons() {
		return this.icons;
	}
	
	public GUIHolder clearIcons() {
		this.icons.clear();
		return this;
	}

	@Override
	public Inventory getInventory() {
		Inventory inventory = Bukkit.createInventory(this, this.size, this.title);
		
		for (Entry<Integer, Icon> entry : this.icons.entrySet()) {
			inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
		}
		
		return inventory;
	}
	
}
