package com.danieli1818.drminigames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.danieli1818.drminigames.arena.kits.BaseKit;
import com.danieli1818.drminigames.arena.kits.Kit;
import com.danieli1818.drminigames.arena.kits.KitsManager;

public class KitsCommands {

	private String commandPrefix;
	
	public KitsCommands(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}
	
	public boolean onCommand(CommandSender sender, String command, String[] args) {
		if (!(command.equalsIgnoreCase("kit") || command.equalsIgnoreCase("kits"))) {
			return false;
		}
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length != 3) {
					System.out.println("Invalid Syntax! Correct Syntax is: /" + commandPrefix + " " + command + " create [id] [name] ...");
					return false;
				}
			}
		}
		return false;
	}
	
	public void createKit(Player player, String name, String id) {
		Kit kit = new BaseKit(player.getInventory(), name, id);
		KitsManager.getInstance().saveKit(id, kit);
	}

	
	
}
