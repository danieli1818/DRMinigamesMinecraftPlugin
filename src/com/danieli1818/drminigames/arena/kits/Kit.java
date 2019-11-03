package com.danieli1818.drminigames.arena.kits;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class Kit {
	
	private Map<Integer, ItemStack> items;
	
	private final int numOfSlots = 18;
	
	private String name;
	
	private String id;
	
	public Kit(String name, String id) {
		this.items = new HashMap<Integer, ItemStack>();
		this.name = name;
		this.id = id;
	}
	
	public void addItem(int slot, ItemStack item) {
		if (item == null) {
			removeItem(slot);
		}
		this.items.put(slot, item);
	}
	
	public void addItem(ItemStack item) {
		for (int i = 0; i < this.numOfSlots; i++) {
			if (!this.items.containsKey(i)) {
				this.items.put(i, item);
			}
		}
	}
	
	public void removeItem(int slot) {
		this.items.remove(slot);
	}
	
	public void removeAllItems() {
		this.items.clear();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getID() {
		return this.id;
	}

}
