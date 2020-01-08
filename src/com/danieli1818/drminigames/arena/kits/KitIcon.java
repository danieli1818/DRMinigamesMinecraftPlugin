package com.danieli1818.drminigames.arena.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.danieli1818.drminigames.utils.guis.ClickAction;
import com.danieli1818.drminigames.utils.guis.Icon;

public class KitIcon implements Icon {
	
	private Kit kit;
	
	private List<ClickAction> clickActions;

	public KitIcon(Kit kit) {
		this.kit = kit;
		this.clickActions = new ArrayList<ClickAction>();
		this.clickActions.add(new ClickAction() {
			
			@Override
			public void execute(Player player) {
				kit.giveToPlayer(player);
				
			}
		});
	}
	
	@Override
	public ItemStack getItemStack() {
		return this.kit.getSymbol();
	}

	@Override
	public List<ClickAction> getClickActions() {
		return this.clickActions;
	}

}
