package com.danieli1818.drminigames.utils.items;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class CommandItemStack {

	private ItemStack itemStack;
	
	private List<Action> leftClickActions;
	
	private List<Action> rightClickActions;
	
	public CommandItemStack(ItemStack itemStack) {
		if (itemStack == null) {
			throw new NullPointerException("itemStack");
		}
		this.itemStack = itemStack;
	}
	
}
