package com.danieli1818.drminigames.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.danieli1818.drminigames.arena.BaseArena;
import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.DRColorShooting;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.DRMinigamePlugin;
import com.danieli1818.drminigames.utils.ArenasManager;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;

public class ArenaCommands implements CommandExecutor {
	
	private static WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	
	private static KitsCommands kitsCommands = new KitsCommands("DRMinigames");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!cmd.getName().equalsIgnoreCase("DRMinigames")) {
			sender.sendMessage("Invalid Command!");
			return false;
		}
		
//		if (args.length <= 0 || args.length > 2) {
//			sender.sendMessage("Invalid Command!");
//			return false;
//		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players Can Use This Command!");
			return false;
		}
		
		Player p = (Player)sender;
		
		if (args.length == 0) {
			p.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "++++++++++++++++DRMinigames Plugin++++++++++++++++");
			p.sendMessage("For help type: /DRMinigames help.");
			return true;
		}
		
//		System.out.println(args.length);
//		for (String string : args) {
//			System.out.println(string);
//		}
		
		if (args[0].equalsIgnoreCase("join")) {
			if (args.length != 2) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames join [ArenaID]");
				return false;
			}
			return joinArena(p, args[1]);
		} else if (args[0].equalsIgnoreCase("create")) {
			if (args.length != 2) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames create [ArenaID]");
				return false;
			}
			return createArena(p, args[1]);
		} else if (args[0].equalsIgnoreCase("leave")) {
			if (args.length != 1) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames leave");
				return false;
			}
			return leaveArena(p);
		} else if (args[0].equalsIgnoreCase("arenas")) {
			if (args.length != 1) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames arenas");
				return false;
			}
			return listArenas(p);
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (args.length != 1) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames reload");
				return false;
			}
			return reload(p);
		} else if (args[0].equalsIgnoreCase("setregion")) {
			if (args.length < 2 || args.length > 3) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames setregion [ArenaID] {RegionID}");
				return false;
			}
			String regionID = null;
			if (args.length == 3) {
				regionID = args[2];
			}
			return setRegion(p, args[1], regionID);
		} else if (args[0].equalsIgnoreCase("setavailability")) {
			if (args.length != 3) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames setavailability [ArenaID] [Availability]");
				return false;
			}
			return setAvailability(p, args[1], Boolean.valueOf(args[2]));
		} else if (args[0].equalsIgnoreCase("setspawn")) {
			if (args.length != 3) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames setspawn [ArenaID] [SpawnName]");
				return false;
			}
			return setSpawn(p, args[1], args[2]);
		} else if (args[0].equalsIgnoreCase("setwaiting")) {
			if (args.length != 2) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames setwaiting [ArenaID]");
				return false;
			}
			return setWaitingLocation(p, args[1]);
		} else if (args[0].equalsIgnoreCase("setleaving")) {
			if (args.length != 2) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames setleaving [ArenaID]");
				return false;
			}
			return setLeavingLocation(p, args[1]);
		} else if (args[0].equalsIgnoreCase("settype")) {
			if (args.length < 3) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames settype [ArenaID] [TypeID] {Args}");
				return false;
			}
			String[] arguments = new String[args.length - 3];
			for (int i = 3; i < args.length; i++) {
				arguments[i - 3] = args[i];
			}
			return setTypeInner(p, args[1], args[2], arguments);
		} else if (args[0].equalsIgnoreCase("saveall")) {
			if (args.length > 1) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames saveall");
				return false;
			}
			return saveAll(p);
		} else if (args[0].equalsIgnoreCase("save")) {
			if (args.length <= 1) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames save Arena1ID Arena2ID Arena3ID Arena4ID ...");
				return false;
			}
			String[] arguments = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				arguments[i - 1] = args[i];
			}
			return saveArenas(p, arguments);
		} else if (args[0].equalsIgnoreCase("setmin")) {
			if (args.length != 3) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames setmin [ArenaID] [Number]");
				return false;
			}
			return setMin(p, args[1], args[2]);
		} else if (args[0].equalsIgnoreCase("setmax")) {
			if (args.length != 3) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames setmax [ArenaID] [Number]");
				return false;
			}
			return setMax(p, args[1], args[2]);
		} else if (args[0].equalsIgnoreCase("stop")) {
			if (args.length != 2) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames stop [ArenaID]");
				return false;
			}
			return stopArena(p, args[1]);
		} else if (args[0].equalsIgnoreCase("command")) {
			if (args.length <= 1) {
				p.sendMessage("Invalid Syntax. Correct Syntax is: /DRMinigames command [ArenaID] ...");
				return false;
			}
			String[] arguments = new String[args.length - 2];
			for (int i = 2; i < args.length; i++) {
				arguments[i - 2] = args[i];
			}
			return sendCommandToArena(p, args[1], arguments);
		} else if (args[0].equalsIgnoreCase("kit") || args[0].equalsIgnoreCase("kits")) {
			String[] arguments = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				arguments[i - 1] = args[i];
			}
			return this.kitsCommands.onCommand(sender, args[0], arguments);
		}
		else {
			sender.sendMessage("Invalid Command!");
			return false;
		}
	}
	
	private boolean joinArena(Player p, String id) {
		if (!p.hasPermission("drminigames.join." + id)) {
			p.sendMessage("You don't have permission to join this arena!");
			return false;
		}
		Arena arena = ArenasManager.getInstance().getArena(id);
		if (arena == null) {
			p.sendMessage("Arena " + id + " does not exist!");
			return false;
		}
		if (!arena.isAvailable()) {
			p.sendMessage("Arena isn't available!");
			return false;
		}
		Arena currentArena = null;
		if ((currentArena = ArenasManager.getInstance().getArena(p.getUniqueId())) != null) {
			if (currentArena == arena) {
				p.sendMessage("Already In The Arena!");
			} else {
				p.sendMessage("Already In An Arena! Type /drminigames leave to leave!");
			}
			return false;
		}
		if (!arena.addPlayer(p.getUniqueId())) {
			p.sendMessage("Already In The Arena!");
			return false;
		}
		p.sendMessage("Successfully Joined Arena " + id + "!");
		return true;
	}
	
	private boolean createArena(Player p, String id) {
		if (!p.hasPermission("drminigames.create")) {
			p.sendMessage("You don't have the permission to execute this command!");
			return false;
		}
		
		// Create Arena
		
		if (ArenasManager.getInstance().doesExist(id)) {
			p.sendMessage("Arena with the name: " + id + " already exists!");
			return false;
		}
		
		ArenasManager.getInstance().addArena(id);
		
		p.sendMessage("DRMinigame Was Created Successfully!");
		
		return true;
	}
	
	private boolean leaveArena(Player p) {
		Arena arena = ArenasManager.getInstance().getArena(p.getUniqueId());
		if (arena == null) {
			p.sendMessage("You aren't in a minigame!");
			return false;
		}
		if (arena.removePlayer(p.getUniqueId())) {
			p.sendMessage("Successfully Left Minigame!");
		} else {
			p.sendMessage("Error Leaving Minigame!");
		}
		return true;
	}
	
	private boolean listArenas(Player p) {
		if (!p.hasPermission("drminigames.listarenas")) {
			p.sendMessage("You don't have permission for this command!");
			return false;
		}
		
		List<String> arenasIDs = ArenasManager.getInstance().getArenasIDs();
		
		p.sendMessage("arenas:");
		for (String arenaID : arenasIDs) {
			p.sendMessage("\t" + arenaID);
		}
		
		return true;
	}
	
	private boolean reload(Player p) {
		if (!p.hasPermission("drminigames.reload")) {
			p.sendMessage("You don't have permission for this command!");
			return false;
		}
		
		ArenasManager.getInstance().reloadArenas();
		return true;
	}
	
	private boolean setRegion(Player p, String arenaID, @Nullable String regionID) {
		
		if (!p.hasPermission("drminigames.setregion." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.setregion." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		try {
			World world = wep.getSession(p).getSelectionWorld();
			Region r = wep.getSession(p).getSelection(world).clone();
//			BlockVector3 min = r.getMinimumPoint();
//			BlockVector3 max = r.getMaximumPoint();
//			Location l1 = new Location(world, min.getBlockX(), min.getBlockY(), min.getBlockZ());
//			Location l2 = new Location(world, max.getBlockX(), max.getBlockY(), max.getBlockZ());
			Arena arena = ArenasManager.getInstance().getArena(arenaID);
			if (arena == null) {
				p.sendMessage("Arena " + arenaID + " doesn't exist!");
				return false;
			}
			arena.setRegion(r, regionID);
		} catch (IncompleteRegionException e) {
			p.sendMessage("Please choose a region for the arena!");
			return false;
		}
		// selection.
		
		p.sendMessage("Region has been successfully set.");
		
		return true;
	}
	
	private boolean setAvailability(Player p , String arenaID, boolean availability) {
		
		if (!p.hasPermission("drminigames.setavailability." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.setavailability." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		if (!availability) {
			arena.setUnavailable();
			
			p.sendMessage("Successfully Set The Arena Unavailable.");
			
			return true;
		}
		
		if (!arena.setAvailable()) {
			
			p.sendMessage("Not Able To Set Available.");
			
			// message for the reason.
			
			return false;
		}
		
		p.sendMessage("Successfuly Set The Arena Available.");
		
		return true;
		
	}
	
	private boolean setSpawn(Player p, String arenaID, String name) {
		
		if (!p.hasPermission("drminigames.setspawn." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.setspawn." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		if (!arena.setSpawnPoint(name, p.getLocation())) {
			p.sendMessage("Please set region first and make sure the spawn location is inside it.");
			return false;
		}
		
		p.sendMessage("Spawn Location Has Been Successfully Set!");
		
		return true;
		
	}
	
	private boolean setWaitingLocation(Player p, String arenaID) {
		
		if (!p.hasPermission("drminigames.setwaiting." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.setwaiting." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		if (!arena.setWaitingLocation(p.getLocation())) {
			p.sendMessage("Error in setting waiting location.");
			
			// message for the reason.
			
			return false;
		}
		
		p.sendMessage("Successfully set the waiting location!");
		
		return true;
		
	}
	
	private boolean setLeavingLocation(Player p, String arenaID) {
		
		if (!p.hasPermission("drminigames.setleaving." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.setleaving." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		if (!arena.setLeavingLocation(p.getLocation())) {
			p.sendMessage("Error in setting leaving location.");
			
			// message for the reason.
			
			return false;
		}
		
		p.sendMessage("Successfully set the leaving location!");
		
		return true;
		
	}
	
	private boolean setType(Player p, String arenaID, String typeName) {
		
		if (!p.hasPermission("drminigames.settype." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.settype." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		Plugin plugin = Bukkit.getPluginManager().getPlugin(typeName);
		
		if (plugin == null) {
			p.sendMessage("Type doesn't exist or its plugin isn't enabled!");
			return false;
		}
		
		if (!(plugin instanceof DRMinigamePlugin)) {
			p.sendMessage("Type's plugin isn't a DRMinigame Plugin!");
			return false;
		}
		
		DRMinigamePlugin drplugin = (DRMinigamePlugin)plugin;
		
		arena.setType(drplugin.getArenaLogic());
		
		p.sendMessage("Arena " + arenaID + "'s type has been set successfully!");
		
		return true;
		
	}
	
	private boolean setTypeInner(Player p, String arenaID, String typeName, String[] args) {
		
		if (!p.hasPermission("drminigames.settype." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.settype." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		if (typeName == null) {
			p.sendMessage("Type doesn't exist!");
			return false;
		}
		if (typeName.equalsIgnoreCase("DRColorShooting")) {
			arena.setType(new DRColorShooting(arena, args));
		} else {
			p.sendMessage("Type doesn't exist!");
			return false;
		}
		
//		arena.setType(drplugin.getArenaLogic());
		
		p.sendMessage("Arena " + arenaID + "'s type has been set successfully!");
		
		return true;
		
	}
	
	private boolean saveAll(Player p) {
		
		if (!p.hasPermission("drminigames.saveall")) {
			p.sendMessage("You don't have permission for this command! (drminigames.saveall)");
			return false;
		}
		
		try {
			ArenasManager.getInstance().saveArenas();
		} catch (IOException e) {
			p.sendMessage("Error saving arenas! Details In Error Log!");
			e.printStackTrace();
			return false;
		}
		
		p.sendMessage("Arenas Have Been Successfully Saved!");
		return true;
		
	}
	
	private boolean saveArenas(Player p, String[] ids) {
		
		if (!p.hasPermission("drminigames.savearenabyid")) {
			p.sendMessage("You don't have permission for this command! (drminigames.savearenabyid)");
			return false;
		}
		
		try {
			ArenasManager.getInstance().saveArenas(ids);
		} catch (IOException e) {
			p.sendMessage("Error saving arenas! Details In Error Log!");
			e.printStackTrace();
			return false;
		}
		
		p.sendMessage("Arenas Have Beean Successfully Saved!");
		return true;
	}
	
	private boolean setMin(Player p, String arenaID, String num) {
		
		if (!p.hasPermission("drminigames.setmin." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.setmin." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		try {
			int number = Integer.valueOf(num);
			if (!arena.setMinNumOfPlayers(number)) {
				p.sendMessage("Number must be bigger than 0!");
				return false;
			}
			p.sendMessage("Successfully Set Min Number Of Players!");
			return true;
		} catch (NumberFormatException exception) {
			p.sendMessage("Not Valid Number!");
			return false;
		}
	}
	
	private boolean setMax(Player p, String arenaID, String num) {
		
		if (!p.hasPermission("drminigames.setmin." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.setmin." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		try {
			int number = Integer.valueOf(num);
			if (!arena.setMaxNumOfPlayers(number)) {
				p.sendMessage("Number must be bigger than 0!");
				return false;
			}
			p.sendMessage("Successfully Set Max Number Of Players!");
			return true;
		} catch (NumberFormatException exception) {
			p.sendMessage("Not Valid Number!");
			return false;
		}
	}
	
	private boolean stopArena(Player p, String arenaID) {
		
		if (!p.hasPermission("drminigames.stop." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.stop." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		return arena.stop();
		
	}
	
	private boolean sendCommandToArena(Player p, String arenaID, String[] args) {
		
		if (!p.hasPermission("drminigames.sendcommand." + arenaID)) {
			p.sendMessage("You don't have permission for this command! (drminigames.sendcommand." + arenaID + ")");
			return false;
		}
		
		if (!ArenasManager.getInstance().doesExist(arenaID)) {
			p.sendMessage("Arena " + arenaID + " doesn't exist!");
			return false;
		}
		
		Arena arena = ArenasManager.getInstance().getArena(arenaID);
		
		arena.sendCommand(p, args);
		
		return true;
		
	}

}
