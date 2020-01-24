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
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length != 3) {
					player.sendMessage("Invalid Syntax! Correct Syntax is: /" + commandPrefix + " " + command + " remove [kitID] [ArenaID]");
					return false;
				}
				removeKit(player, args[1], args[2]);
			} else if (args[0].equalsIgnoreCase("delete")) {
				if (args.length != 2) {
					player.sendMessage("Invalid Syntax! Correct Syntax is: /" + commandPrefix + " " + command + " delete [kitID]");
					return false;
				}
				deleteKit(player, args[1]);
			}
		} else {
			return false;
		}
		return true;
	}
	
	public void createKit(Player player, String name, String id) {
		if (!player.hasPermission("drminigames.kits.create")) {
			player.sendMessage("You don't have permission for this command! (drminigames.kits.create)");
			return;
		}
		if (KitsManager.getInstance().containsKit(id)) {
			player.sendMessage("Kit With This ID Already Exists!");
		}
		Kit kit = new BaseKit(player.getInventory(), name, id);
		if (KitsManager.getInstance().saveKit(id, kit)) {
			player.sendMessage("Saved Kit " + id + " Successfully!");
		}
		
	}
	
	public void addKit(Player player, String kitID, String ArenaID) {
		if (!player.hasPermission("drminigames.kits.add")) {
			player.sendMessage("You don't have permission for this command! (drminigames.kits.add)");
			return;
		}
		Arena arena = ArenasManager.getInstance().getArena(ArenaID);
		if (arena == null) {
			player.sendMessage("No Arena With The ID: " + ArenaID + " Exists!");
			return;
		}
		Kit kit = KitsManager.getInstance().loadKit(kitID);
		if (kit == null) {
			player.sendMessage("No Kit With The ID: " + kitID + " Exists!");
			return;
		}
		arena.addKit(kit);
		player.sendMessage("Successfully Added Kit With The ID " + kitID);
	}
	
	public void setKitSymbol(Player player, String kitID) {
		if (!player.hasPermission("drminigames.kits.set.symbol")) {
			player.sendMessage("You don't have permission for this command! (drminigames.kits.set.symbol)");
			return;
		}
		Kit kit = KitsManager.getInstance().loadKit(kitID);
		if (kit == null) {
			player.sendMessage("No Kit With The ID: " + kitID + " Exists!");
			return;
		}
		kit.setSymbol(player.getItemInHand());
		KitsManager.getInstance().saveKit(kitID, kit);
		player.sendMessage("Successfully Set Kit Symbol For Kit With The ID " + kitID);
	}
	
	public void removeKit(Player player, String kitID, String arenaID) {
		if (!player.hasPermission("drminigames.kits.remove")) {
			player.sendMessage("You don't have permission for this command! (drminigames.kits.remove)");
			return;
		}
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		if (arena == null) {
			player.sendMessage("No Arena With The ID: " + arenaID + " Exists!");
		}
		Kit kit = KitsManager.getInstance().loadKit(kitID);
		if (kit == null) {
			player.sendMessage("No Kit With The ID: " + kitID + " Exists!");
			return;
		}
		if (!arena.removeKit(kit)) {
			player.sendMessage("No Kit With The ID: " + kitID + " Is In The Arena With The ID: " + arenaID);
			return;
		}
		player.sendMessage("Successfully Removed Kit!");
		
	}
	
	public void deleteKit(Player player, String kitID) {
		if (!player.hasPermission("drminigames.kits.delete")) {
			player.sendMessage("You don't have permission for this command! (drminigames.kits.delete)");
			return;
		}
		Kit kit = KitsManager.getInstance().loadKit(kitID);
		if (kit == null) {
			player.sendMessage("No Kit With The ID: " + kitID + " Exists!");
			return;
		}
		if (!KitsManager.getInstance().removeKit(kitID)) {
			player.sendMessage("There Has Been An Error Deleting The Kit!");
			return;
		}
		player.sendMessage("Successfully Deleted Kit!");
	}

	
	
}
