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

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.RegionUtils;
import com.google.common.collect.BiMap;

public class DRColorShooting implements ArenaLogic {
	
	private Arena arena;
	private List<String> teamColors;
	private BiMap<UUID, String> playersColors;
	private BiMap<String, List<Material>> teamColorsBlocks;
	private Map<Material, Integer> blocksPoints;
	private int numOfBlocksPerTeam;
	
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
	}
	
	public DRColorShooting(Arena arena) {
		this.arena = arena;
		this.teamColors = new ArrayList<String>();
	}

	@Override
	public void start(Arena arena) {
		setTeamsToPlayers();
		teleportPlayersToArena();
		
	}

	@Override
	public boolean canBeAvailable(Arena arena) {
		Map<String, Location> spawns = arena.getSpawnLocation();
		for (String color : teamColors) {
			if (!spawns.containsKey(color) || spawns.get(color) == null) {
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
		List<Location> locations = RegionUtils.getRandomNBlocksInRegion(this.arena.getLimits(), this.numOfBlocksPerTeam * this.teamColors.size(), (Location location) -> {
			Block block = location.getBlock();
			return block == null || block.getType() == Material.AIR;
		});
		Iterator currentTeamColor = this.teamColors.iterator();
		for (Location location : locations) {
			if (!currentTeamColor.hasNext()) {
				currentTeamColor = this.teamColors.iterator();
			}
			location.getBlock().setType(this.teamColorsBlocks.get(currentTeamColor.next()).get(0));
		}
	}
	
	private Material getRandomMaterialOfTeam(String team) {
		return null;
	}

}
