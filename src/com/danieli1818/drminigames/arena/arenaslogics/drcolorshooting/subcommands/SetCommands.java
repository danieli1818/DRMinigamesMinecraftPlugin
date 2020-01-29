package com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.subcommands;

import org.bukkit.entity.Player;

import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.DRColorShooting;

public class SetCommands {
	
	private DRColorShooting arenaLogic;
	
	public SetCommands(DRColorShooting arenaLogic) {
		this.arenaLogic = arenaLogic;
	}
	
	public boolean commands(Player player, String subCommand, String[] args) {
		
		if (subCommand == null) {
			helpCommand(player, 1);
			return true;
		} else if (subCommand.equalsIgnoreCase("numOfBlocksPerTeam")) {
			if (args.length != 2) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] set numOfBlocksPerTeam [Number]");
				return true;
			}
			setNumOfBlocksPerTeam(player, args[1]);
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
		} else if (subCommand.equalsIgnoreCase("timeForGame")) {
			if (args.length != 2) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] set timeForGame [Time In Seconds]");
			}
			setTimeForGame(player, args[1]);
			return true;
		} else {
			System.out.println("Invalid Command! For Help Type: /drminigames command [ArenaID] set help!");
			return false;
		}

		
	}
	
	private void helpCommand(Player player, int page) {
		if (page == 1) {
			player.sendMessage("/drminigames command [ArenaID] set help [Page Number] - Show This Page!");
			player.sendMessage("/drminigames command [ArenaID] set numOfBlocksPerTeam [Number] - Set Number Of Blocks Spawning For Each Team!");
		} else {
			player.sendMessage("Not Valid Page Number!");
		}
		
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
