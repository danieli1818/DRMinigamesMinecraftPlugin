package com.danieli1818.drminigames.arena.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import com.danieli1818.drminigames.utils.SavingAndLoadingUtils;

public class Kit implements ConfigurationSerializable {
	
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

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("items", SavingAndLoadingUtils.stringify(this.items));
		map.put("id", this.id);
		map.put("name", this.name);
		return map;
	}
	
	public static Kit deserialize(Map<String, Object> map) {
		if (map.get("id") == null || !(map.get("id") instanceof String) || map.get("name") == null || !(map.get("name") instanceof String)) {
			return null;
		}
		Kit kit = new Kit((String)map.get("name"), (String)map.get("id"));
		if (map.get("items") == null && map.get("items") instanceof Map<?, ?>) {
			kit.items = SavingAndLoadingUtils.integerify((Map<String, ItemStack>)map.get("items"));
		}
		return kit;
	}

}
