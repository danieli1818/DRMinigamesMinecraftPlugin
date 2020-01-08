package com.danieli1818.drminigames.arena.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.danieli1818.drminigames.utils.SavingAndLoadingUtils;

public class BaseKit implements Kit {
	
	private Map<Integer, ItemStack> items;
	
	private final int numOfSlots = 18;
	
	private String name;
	
	private String id;
	
	private ItemStack symbol;
	
	public BaseKit(String name, String id) {
		this.items = new HashMap<Integer, ItemStack>();
		this.name = name;
		this.id = id;
	}
	
	public BaseKit(PlayerInventory inventory, String name, String id) {
		this(name, id);
		int index = 0;
		for (ItemStack itemStack : inventory.getContents()) {
			addItem(itemStack);
			index++;
		}
	}
	
	public void addItem(int slot, ItemStack item) {
		if (item == null) {
			removeItem(slot);
		}
		this.items.put(slot, item);
	}
	
	public boolean addItem(ItemStack item) {
		for (int i = 0; i < this.numOfSlots; i++) {
			if (!this.items.containsKey(i)) {
				this.items.put(i, item);
				return true;
			}
		}
		return false;
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
		map.put("symbol", this.symbol);
		return map;
	}
	
	public static BaseKit deserialize(Map<String, Object> map) {
		if (map.get("id") == null || !(map.get("id") instanceof String) || map.get("name") == null || !(map.get("name") instanceof String)) {
			return null;
		}
		BaseKit kit = new BaseKit((String)map.get("name"), (String)map.get("id"));
		if (map.get("items") != null && map.get("items") instanceof Map<?, ?>) {
			kit.items = SavingAndLoadingUtils.integerify((Map<String, ItemStack>)map.get("items"));
		}
		if (map.get("symbol") != null && map.get("symbol") instanceof ItemStack) {
			kit.symbol = (ItemStack)map.get("symbol");
		}
		return kit;
	}

	@Override
	public void giveToPlayer(Player player) {
		
		PlayerInventory inventory = player.getInventory();
		
		inventory.clear();
		
		for (Entry<Integer, ItemStack> entry : this.items.entrySet()) {
			inventory.setItem(entry.getKey(), entry.getValue());
		}
		
	}

	@Override
	public ItemStack getSymbol() {
		return this.symbol;
	}
	
	@Override
	public void setSymbol(ItemStack symbol) {
		this.symbol = symbol;
	}

}
