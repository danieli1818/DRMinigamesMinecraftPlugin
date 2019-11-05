package com.danieli1818.drminigames.arena.arenaslogics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.bukkit.Location;

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;

public class DRColorShooting implements ArenaLogic {
	
	private List<String> teamColors;
	
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
		// TODO Auto-generated method stub
		
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
