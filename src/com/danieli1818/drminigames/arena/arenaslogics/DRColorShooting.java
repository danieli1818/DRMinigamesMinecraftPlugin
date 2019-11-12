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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.RegionUtils;

public class DRColorShooting implements ArenaLogic {
	
	private List<String> teamColors;
	private HashMap<UUID, String> playersColors;
	private HashMap<String, TeamColorBlock> teamColorsBlocks;
	private int numOfBlocksPerTeam;
	
	private class TeamColorBlock {
		
		private Block block;
		private int points;
		
		public TeamColorBlock(Block block, int points) {
			this.block = block;
			this.points = points;
		}
		
	}
	
	public DRColorShooting(String[] args) {
		this(Arrays.asList(args));
	}
	
	public DRColorShooting(List<String> teamColors) {
		this.teamColors = teamColors;
	}
	
	public DRColorShooting() {
		this.teamColors = new ArrayList<String>();
	}

	@Override
	public void start(Arena arena) {
		List<UUID> uuids = arena.getPlayers();
		Collections.shuffle(uuids);
		Iterator<String> currentTeamColor = teamColors.iterator();
		for (UUID uuid : uuids) {
			if (!currentTeamColor.hasNext()) {
				currentTeamColor = teamColors.iterator();
			}
			this.playersColors.put(uuid, currentTeamColor.next());
		}
		List<Location> locations = RegionUtils.getRandomNBlocksInRegion(arena.getLimits(), this.numOfBlocksPerTeam * this.teamColors.size(), (Location location) -> {
			Block block = location.getBlock();
			return block == null || block.getType() == Material.AIR;
		});
		for (Location location : locations) {
			
		}
		
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

}
