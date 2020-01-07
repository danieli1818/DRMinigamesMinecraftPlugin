package com.danieli1818.drminigames.utils.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Icon {

	private final ItemStack itemStack;
	
	private final List<ClickAction> clickActions;
	
	public Icon(ItemStack itemStack) {
		this.itemStack = itemStack;
		this.clickActions = new ArrayList<ClickAction>();
	}
	
	public ItemStack getItemStack() {
		return this.itemStack;
	}
	
	public Icon addClickAction(ClickAction clickAction) {
		this.clickActions.add(clickAction);
		return this;
	}
	
	public Icon removeClickAction(ClickAction clickAction) {
		this.clickActions.remove(clickAction);
		return this;
	}
	
	public Icon clearClickActions() {
		this.clickActions.clear();
		return this;
	}
	
	public List<ClickAction> getClickActions() {
		return this.clickActions;
	}
	
}
