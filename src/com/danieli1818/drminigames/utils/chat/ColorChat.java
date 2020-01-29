package com.danieli1818.drminigames.utils.chat;

import org.bukkit.ChatColor;

public class ColorChat {

	public static String chat(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static String[] chat(String[] messages) {
		String[] coloredMessages = new String[messages.length];
		int i = 0;
		for (String message : messages) {
			coloredMessages[i] = ChatColor.translateAlternateColorCodes('&', message);
			i++;
		}
		return coloredMessages;
	}
	
}
