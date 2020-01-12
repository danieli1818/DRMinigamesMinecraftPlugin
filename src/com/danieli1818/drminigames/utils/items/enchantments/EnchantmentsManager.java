package com.danieli1818.drminigames.utils.items.enchantments;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantmentsManager {
	
	private static Map<Class, Enchantment> customEnchantmentsIDs = new HashMap<Class, Enchantment>();
	
	private static void registerEnchantment(Function<Integer, Enchantment> enchantmentCreator) {
		
		try {
			
			Field acceptingField = Enchantment.class.getDeclaredField("acceptingNew");
			acceptingField.setAccessible(true);
			acceptingField.set(null, true);
			
			int id = Enchantment.values().length;
			
			Enchantment enchantment = enchantmentCreator.apply(id);
			
			Enchantment.registerEnchantment(enchantment);
			
			customEnchantmentsIDs.put(enchantment.getClass(), enchantment);
			
		} catch (Exception exception) {
			exception.printStackTrace();
		}
				
	}
	
	public static void registerAllEnchantments() {
		
		registerEnchantment((Integer id) -> {
			return new Glow(id);
		});
		
	}
	
	public static boolean addEnchantment(Class<? extends Enchantment> enchantmentClass, ItemStack itemStack, int level) {
		
		Enchantment enchantment = customEnchantmentsIDs.get(enchantmentClass);
		
		if (enchantment == null) {
			return false;
		}
		
		itemStack.addEnchantment(enchantment, level);
		return true;
		
	}
	
	public static boolean addEnchantmentFirstLevel(Class<? extends Enchantment> enchantmentClass, ItemStack itemStack) {
		
		Enchantment enchantment = customEnchantmentsIDs.get(enchantmentClass);
		
		if (enchantment == null) {
			return false;
		}
		
		itemStack.addEnchantment(enchantment, enchantment.getStartLevel());
		return true;
		
	}
	
	public static boolean addEnchantmentMaxLevel(Class<? extends Enchantment> enchantmentClass, ItemStack itemStack) {
		
		Enchantment enchantment = customEnchantmentsIDs.get(enchantmentClass);
		
		if (enchantment == null) {
			return false;
		}
		
		itemStack.addEnchantment(enchantment, enchantment.getMaxLevel());
		return true;
		
	}
	
}
