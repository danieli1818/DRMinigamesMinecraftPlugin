package com.danieli1818.drminigames.utils.guis;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public interface Icon {

	public ItemStack getItemStack();
	
	public List<ClickAction> getClickActions();
	
}
