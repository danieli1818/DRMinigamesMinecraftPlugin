package com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.subcommands;

import org.bukkit.entity.Player;

import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.DRColorShooting;

public class SetCommands {
	
	private DRColorShooting arenaLogic;
	
	public SetCommands(DRColorShooting arenaLogic) {
		this.arenaLogic = arenaLogic;
	}
	
	public void commands(Player player, String subCommand, String[] args) {
		
		if (subCommand == null) {
			helpCommand(player, 1);
		} else if (subCommand.equalsIgnoreCase("prefix")) {
			if (args.length != 3) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] set prefix [TeamID] [Prefix]");
				return;
			}
			setPrefix(player, args[1], args[2]);
		} else if (subCommand.equalsIgnoreCase("numOfBlocksPerTeam")) {
			if (args.length != 2) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] set numOfBlocksPerTeam [Number]");
				return;
			}
			setNumOfBlocksPerTeam(player, args[1]);
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
		} else if (subCommand.equalsIgnoreCase("timeForGame")) {
			if (args.length != 2) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] set timeForGame [Time In Seconds]");
			}
			setTimeForGame(player, args[1]);
		} else {
			helpCommand(player, 1);
		}

		
	}
	
	private void helpCommand(Player player, int page) {
		if (page == 1) {
			player.sendMessage("/drminigames command [ArenaID] set help [Page Number] - Show This Page!");
			player.sendMessage("/drminigames command [ArenaID] set numOfBlocksPerTeam [Number] - Set Number Of Blocks Spawning For Each Team!");
			player.sendMessage("/drminigames command [ArenaID] set prefix [TeamID] [Prefix] - Set Prefix Of Team.");
		} else {
			player.sendMessage("Not Valid Page Number!");
		}
		
	}
	
	private void setPrefix(Player player, String teamID, String prefix) {
		if (!this.arenaLogic.containsTeam(teamID)) {
			player.sendMessage("Team " + teamID + " doesn't exist!");
			return;
		}
		this.arenaLogic.addPrefixToTeam(teamID, prefix);
		player.sendMessage("Successfully Set Prefix!");
	}
	
	private void setNumOfBlocksPerTeam(Player player, String number) {
		try {
			int num = Integer.parseInt(number);
			this.arenaLogic.setNumOfBlocksPerTeam(num);
		} catch (Exception e) {
			player.sendMessage("Not Valid Number! Number Should Be An Integer And Bigger Than 0!");
		}
	}
	
	private void setTimeForGame(Player player, String timeInSecs) {
		try {
			int num = Integer.parseInt(timeInSecs);
			this.arenaLogic.setTimeForGame(num);
		} catch (Exception e) {
			player.sendMessage("Not Valid Time In Seconds! Time In Seconds Must Be A Number And Bigger Than 0!");
		}
	}
}
