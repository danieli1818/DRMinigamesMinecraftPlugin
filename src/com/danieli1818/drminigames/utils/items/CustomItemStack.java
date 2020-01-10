package com.danieli1818.drminigames.utils.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;

public class CustomItemStack {
	
	private NBTItem nbtItemStack;

	public CustomItemStack(ItemStack itemStack) {
		
		if (itemStack == null) {
			throw new NullPointerException("itemStack");
		}
		this.nbtItemStack = new NBTItem(itemStack);
		this.nbtItemStack.setBoolean("CustomItemStack", true);
		
	}
	
	public CustomItemStack setType(String type) {
		
		this.nbtItemStack.setString("Type", type);
		
		return this;
		
	}
	
	public String getType(String type) {
		
		return this.nbtItemStack.getString("Type");
		
	}
	
	public static boolean isCustomItemStack(ItemStack itemStack) {
		
		NBTItem nbtItemStack = new NBTItem(itemStack);
		
		if (nbtItemStack.hasNBTData()) {
			if (nbtItemStack.getBoolean("CustomItemStack") != null) {
				return nbtItemStack.getBoolean("CustomItemStack");
			}
		}
		return false;
		
	}
	
	public ItemStack getItemStack() {
		
		return this.nbtItemStack.getItem();
		
	}
	
	
	
}
