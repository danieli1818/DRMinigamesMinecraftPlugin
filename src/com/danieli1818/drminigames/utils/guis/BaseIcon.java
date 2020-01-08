package com.danieli1818.drminigames.utils.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class BaseIcon implements Icon {

	private final ItemStack itemStack;
	
	private final List<ClickAction> clickActions;
	
	public BaseIcon(ItemStack itemStack) {
		this.itemStack = itemStack;
		this.clickActions = new ArrayList<ClickAction>();
	}
	
	public ItemStack getItemStack() {
		return this.itemStack;
	}
	
	public BaseIcon addClickAction(ClickAction clickAction) {
		this.clickActions.add(clickAction);
		return this;
	}
	
	public BaseIcon removeClickAction(ClickAction clickAction) {
		this.clickActions.remove(clickAction);
		return this;
	}
	
	public BaseIcon clearClickActions() {
		this.clickActions.clear();
		return this;
	}
	
	public List<ClickAction> getClickActions() {
		return this.clickActions;
	}
	
}
