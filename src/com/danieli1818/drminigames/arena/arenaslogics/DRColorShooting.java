package com.danieli1818.drminigames.arena.arenaslogics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.RegionUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sk89q.worldedit.regions.Region;

public class DRColorShooting implements ArenaLogic {
	
	private Arena arena;
	private List<String> teamColors;
	private BiMap<UUID, String> playersColors;
	private BiMap<String, List<Material>> teamColorsBlocks;
	private Map<Material, Integer> blocksPoints;
	private int numOfBlocksPerTeam;
	private Thread thread;
	private volatile Boolean shouldStop;
	
//	private class TeamColorBlock {
//		
//		private Block block;
//		private int points;
//		
//		public TeamColorBlock(Block block, int points) {
//			this.block = block;
//			this.points = points;
//		}
//		
//	}
	
	public DRColorShooting(Arena arena, String[] args) {
		this(arena, Arrays.asList(args));
	}
	
	public DRColorShooting(Arena arena, List<String> teamColors) {
		this.arena = arena;
		this.teamColors = teamColors;
		this.playersColors = HashBiMap.create();
		this.teamColorsBlocks = HashBiMap.create();
		this.blocksPoints = new HashMap<Material, Integer>();
	}
	
	public DRColorShooting(Arena arena) {
		this.arena = arena;
		this.teamColors = new ArrayList<String>();
		this.playersColors = HashBiMap.create();
		this.teamColorsBlocks = HashBiMap.create();
		this.blocksPoints = new HashMap<Material, Integer>();
	}

	@Override
	public void start(Arena arena) {
		synchronized(this.shouldStop) {
			this.shouldStop = false;
			this.shouldStop.notifyAll();
		}
		setCurrentThread();
		setTeamsToPlayers();
		teleportPlayersToArena();
		spawnRandomTeamBlocks();
		synchronized(this.shouldStop) {
			if (!this.shouldStop) {
				while (!this.shouldStop) {
					try {
						this.shouldStop.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}

			}
		}
		finish();
		
	}

	@Override
	public boolean canBeAvailable(Arena arena) {
		Map<String, Location> spawns = arena.getSpawnLocation();
		Map<String, Region> regions = arena.getRegions();
		for (String color : teamColors) {
			if (spawns.get(color) == null || regions.get(color) == null) {
				return false;
			}
		}
		if (arena.getLimits() == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		ArenaLogic.super.update(o, arg);
	}

	@Override
	public String getID() {
		return "DRColorShooting";
	}

	@Override
	public void loadArenaLogicFromMap(Map<String, String> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getArenaLogicMap() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void setTeamsToPlayers() {
		List<UUID> uuids = this.arena.getPlayers();
		Collections.shuffle(uuids);
		Iterator<String> currentTeamColor = teamColors.iterator();
		for (UUID uuid : uuids) {
			if (!currentTeamColor.hasNext()) {
				currentTeamColor = teamColors.iterator();
			}
			this.playersColors.put(uuid, currentTeamColor.next());
		}
	}
	
	private void teleportPlayersToArena() {
		List<UUID> uuids = this.arena.getPlayers();
		Server server = Bukkit.getServer();
		Map<String, Location> spawns = this.arena.getSpawnLocation();
		for (UUID uuid : uuids) {
			String teamColor = this.playersColors.get(uuid);
			Player player = server.getPlayer(uuid);
			if (player != null) {
				player.teleport(spawns.get(teamColor));
			}
		}
	}
	
	private void spawnRandomTeamBlocks() {
		Map<String, Region> regions = this.arena.getRegions();
		Map<String, List<Location>> locationsPerRegion = new HashMap<String, List<Location>>();
		for (String color : teamColors) {
			
			List<Location> locations = RegionUtils.getRandomNBlocksInRegion(regions.get(color), this.numOfBlocksPerTeam, (Location location) -> {
				Block block = location.getBlock();
				return block == null || block.getType() == Material.AIR;
			});
			
			if (locations != null) {
				locationsPerRegion.put(color, locations);
			}
			
		}

		
		Iterator currentTeamColor = this.teamColors.iterator();
		for (List<Location> locations : locationsPerRegion.values()) {
			for (Location location : locations) {
				if (!currentTeamColor.hasNext()) {
					currentTeamColor = this.teamColors.iterator();
				}
				location.getBlock().setType(getRandomMaterialOfTeam((String)currentTeamColor.next()));
			}
		}

	}
	
	private Material getRandomMaterialOfTeam(String team) {
		return this.teamColorsBlocks.get(team).get(0);
	}
	
	public boolean stop() {
		synchronized(this.shouldStop) {
			this.shouldStop = true;
			this.shouldStop.notifyAll();
		}
		return true;
	}
	
	public void forceStop() {
		stop();
		if (this.thread != null && this.thread.isAlive()) {
			this.thread.interrupt();
		}
	}
	
	private void setCurrentThread() {
		this.thread = Thread.currentThread();
	}
	
	private void finish() {
		
	}
	
	public void command(Player player, String[] args) {
		if (args.length <= 0) {
			return;
		}
		
		String command = args[0];
		
		if (command.equalsIgnoreCase("addblock")) {
			if (args.length < 2 || args.length > 3) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] addblock [TeamID] {Block}");
				return;
			}
		}
	}
	
	private void addBlock(Player player, String teamID, String block) {
		
		if (!player.hasPermission("drminigames.drcolorshooting.addblock." + this.arena.getID())) {
			player.sendMessage("You don't have permission to run this command! (drminigames.drcolorshooting.addblock." + this.arena.getID() + ")");
			return;
		}
		
		if (!this.teamColors.contains(teamID)) {
			player.sendMessage("Team " + teamID + " doesn't exist!");
		}
		
		if (block == null) {
			
			ItemStack holdingItem = player.getInventory().getItemInMainHand();
			
			if (holdingItem == null) {
				player.sendMessage("You didn't type block type nor hold a block in your main hand!");
				return;
			}
			
		}
	}

}
