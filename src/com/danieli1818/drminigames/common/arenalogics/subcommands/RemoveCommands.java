package com.danieli1818.drminigames.common.arenalogics.subcommands;

import org.bukkit.entity.Player;

import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.DRColorShooting;
import com.danieli1818.drminigames.common.arenalogics.TeamsArenaLogic;

public class RemoveCommands {

	private TeamsArenaLogic arenaLogic;
	
	public RemoveCommands(TeamsArenaLogic arenaLogic) {
		this.arenaLogic = arenaLogic;
	}
	
	public void commands(Player player, String subCommand, String[] args) {
		if (subCommand == null) {
			helpCommand(player, 1);
		} else if (subCommand.equalsIgnoreCase("teams")) {
			if (args.length < 2) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] remove teams [TeamID1] [TeamID2] [TeamID3] [TeamID4] ...");
				return;
			}
			for (int i = 1; i < args.length; i++) {
				if (!removeTeam(player, args[i])) {
					continue;
				}
			}
		}
	}
	
	private boolean removeTeam(Player player, String name) {
		if (!player.hasPermission("drminigames.drcolorshooting.remove.team." + this.arenaLogic.getArenaID())) {
			player.sendMessage("You don't have permission for this command! (" + "drminigames.drcolorshooting.remove.team." + this.arenaLogic.getArenaID() + ")");
			return false;
		}
		if (!this.arenaLogic.removeTeam(name)) {
			player.sendMessage("Team " + name + " Didn't Exist!");
			return false;
		}
		player.sendMessage("Successfully Removed Team " + name + "!");
		return true;
	}
	
	private void helpCommand(Player player, int page) {
		if (page == 1) {
			player.sendMessage("/drminigames command [ArenaID] remove help [Page Number] - Show This Page!");
			player.sendMessage("/drminigames command [ArenaID] remove teams [TeamID1] [TeamID2] [TeamID3] [TeamID4] ... - Remove Teams.");
		} else {
			player.sendMessage("Not Valid Page Number!");
		}
	}
	
	
}
