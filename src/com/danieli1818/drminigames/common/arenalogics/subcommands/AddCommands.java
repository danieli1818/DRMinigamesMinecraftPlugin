package com.danieli1818.drminigames.common.arenalogics.subcommands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.BlockPointsInformation;
import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.DRColorShooting;
import com.danieli1818.drminigames.common.BlockInformation;
import com.danieli1818.drminigames.common.arenalogics.TeamsArenaLogic;

public class AddCommands {

	private TeamsArenaLogic arenaLogic;
	
	public AddCommands(TeamsArenaLogic arenaLogic) {
		this.arenaLogic = arenaLogic;
	}
	
	public boolean commands(Player player, String subCommand, String[] args) {
		
		if (subCommand == null) {
			helpCommand(player, 1);
			return true;
		} else if (subCommand.equalsIgnoreCase("teams")) {
			if (args.length < 2) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] add teams [TeamID1] [TeamID2] [TeamID3] [TeamID4] ...");
				return true;
			}
			for (int i = 1; i < args.length; i++) {
				if (!addTeam(player, args[i])) {
					continue;
				}
			}
			return true;
		} else if (subCommand.equalsIgnoreCase("rewardsCommand")) {
			if (args.length < 3) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] add rewardsCommand [Place] [Command]");
				return true;
			}
			addRewardsCommand(player, Integer.parseInt(args[1]), Arrays.copyOfRange(args, 2, args.length));
			return true;
		} else if (subCommand.equalsIgnoreCase("help")) {
			if (args.length != 2) {
				helpCommand(player, 1);
			} else {
				try {
					int page = Integer.parseInt(args[1]);
					helpCommand(player, page);
				} catch (NumberFormatException e) {
					player.sendMessage("Invalid Syntax! Not Valid Number!");
				}
			}
			return true;
		}
		return false;
//		else {
//			System.out.println("Invalid Command! For Help Type: /drminigames command [ArenaID] add help!");
//		}
	}
	
	private boolean addTeam(Player player, String name) {
		if (!player.hasPermission("drminigames.drcolorshooting.add.team." + this.arenaLogic.getArenaID())) {
			player.sendMessage("You don't have permission for this command! (drminigames.drcolorshooting.add.team." + this.arenaLogic.getArenaID() + ")");
			return false;
		}
		if (!this.arenaLogic.addTeam(name)) {
			player.sendMessage("Team " + name + " already exists!");
			return false;
		}
		player.sendMessage("Team " + name + " has been added successfully!");
		return true;
	}
	
	private void helpCommand(Player player, int page) {
		if (page == 1) {
			player.sendMessage("/drminigames command [ArenaID] add help [Page Number] - Show This Page!");
			player.sendMessage("/drminigames command [ArenaID] add block [TeamID] [Points] {Block} - Add Block For Team!");
			player.sendMessage("/drminigames command [ArenaID] add teams [TeamID1] [TeamID2] [TeamID3] [TeamID4] ... - Add Teams.");
		} else {
			player.sendMessage("Not Valid Page Number!");
		}
	}
	
	private boolean addRewardsCommand(Player player, int place, String[] command) {
		if (!player.hasPermission("drminigames.drcolorshooting.add.rewardscommand." + this.arenaLogic.getArenaID())) {
			player.sendMessage("You don't have permission for this command! (drminigames.drcolorshooting.add.rewardscommand." + this.arenaLogic.getArenaID() + ")");
			return false;
		}
		this.arenaLogic.addRewardCommand(place, String.join(" ", command));
		return true;
	}
	
}
