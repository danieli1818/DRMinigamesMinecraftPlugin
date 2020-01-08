package com.danieli1818.drminigames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.danieli1818.drminigames.arena.kits.BaseKit;
import com.danieli1818.drminigames.arena.kits.Kit;
import com.danieli1818.drminigames.arena.kits.KitsManager;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.utils.ArenasManager;

public class KitsCommands {

	private String commandPrefix;
	
	public KitsCommands(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}
	
	public boolean onCommand(CommandSender sender, String command, String[] args) {
		if (!(command.equalsIgnoreCase("kit") || command.equalsIgnoreCase("kits"))) {
			return false;
		}
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player)sender;
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length < 3) {
					player.sendMessage("Invalid Syntax! Correct Syntax is: /" + commandPrefix + " " + command + " create [KitID] [Name] ...");
					return false;
				}
				String name = args[2];
				for (int i = 3; i < args.length; i++) {
					name += " " + args[i];
				}
				createKit(player, name, args[1]);
			} else if (args[0].equalsIgnoreCase("add")) {
				if (args.length != 3) {
					player.sendMessage("Invalid Syntax! Correct Syntax is: /" + commandPrefix + " " + command + " add [kitID] [ArenaID]");
					return false;
				}
				addKit(player, args[1], args[2]);
			} else if (args[0].equalsIgnoreCase("set")) {
				if (args.length <= 1) {
					player.sendMessage("Invalid Command!");
					return false;
				}
				if (args[1].equalsIgnoreCase("symbol")) {
					if (args.length != 3) {
						player.sendMessage("Invalid Syntax! Correct Syntax is: /" + commandPrefix + " " + command + " set symbol [kitID]");
						return false;
					}
					setKitSymbol(player, args[2]);
				}
			}
		} else {
			return false;
		}
		return true;
	}
	
	public void createKit(Player player, String name, String id) {
		Kit kit = new BaseKit(player.getInventory(), name, id);
		KitsManager.getInstance().saveKit(id, kit);
	}
	
	public void addKit(Player player, String kitID, String ArenaID) {
		Arena arena = ArenasManager.getInstance().getArena(ArenaID);
		Kit kit = KitsManager.getInstance().loadKit(kitID);
		if (kit == null) {
			player.sendMessage("No Kit With The ID: " + kitID + " Exists!");
			return;
		}
		arena.addKit(kit);
	}
	
	public void setKitSymbol(Player player, String kitID) {
		Kit kit = KitsManager.getInstance().loadKit(kitID);
		if (kit == null) {
			player.sendMessage("No Kit With The ID: " + kitID + " Exists!");
			return;
		}
		kit.setSymbol(player.getItemInHand());
		KitsManager.getInstance().saveKit(kitID, kit);
	}

	
	
}
