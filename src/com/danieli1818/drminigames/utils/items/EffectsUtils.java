package com.danieli1818.drminigames.utils.items;

//import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.danieli1818.drminigames.utils.items.enchantments.EnchantmentsManager;
import com.danieli1818.drminigames.utils.items.enchantments.Glow;

//import net.minecraft.server.v1_12_R1.NBTTagCompound;
//import net.minecraft.server.v1_12_R1.NBTTagList;

public class EffectsUtils {

	public static ItemStack addGlow(ItemStack itemStack) {
		
		EnchantmentsManager.addEnchantmentFirstLevel(Glow.class, itemStack);
		
		return itemStack;
		
//		net.minecraft.server.v1_12_R1.ItemStack itemStackCopy = CraftItemStack.asNMSCopy(itemStack);
//		
//		ItemStack returnItemStack = CraftItemStack.asCraftMirror(itemStackCopy);
//		
//		if (!EnchantmentsManager.addEnchantmentFirstLevel(Glow.class, returnItemStack)) {
//			return null;
//		}
//		
//		return returnItemStack;
		
	}
	
}
