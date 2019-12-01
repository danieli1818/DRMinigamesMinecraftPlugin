package com.danieli1818.drminigames.arena.arenaslogics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Observable;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.RegionUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimaps;
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
	private Random rnd;
	private Map<String, String> teamColorsPrefixes;
	private Scoreboard board;
	private NavigableMap<Integer, List<String>> rewardsCommands;
	
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
		this.rnd = new Random();
		this.teamColorsPrefixes = new HashMap<String, String>();
		this.rewardsCommands = new TreeMap<Integer, List<String>>();
	}
	
	public DRColorShooting(Arena arena) {
		this.arena = arena;
		this.teamColors = new ArrayList<String>();
		this.playersColors = HashBiMap.create();
		this.teamColorsBlocks = HashBiMap.create();
		this.blocksPoints = new HashMap<Material, Integer>();
		this.rnd = new Random();
		this.teamColorsPrefixes = new HashMap<String, String>();
		this.rewardsCommands = new TreeMap<Integer, List<String>>();
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
		this.board = initializeScoreboard(this.teamColors);
		setSidebarScoreboardToPlayers();
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
		if (!this.arena.isRunning()) {
			return;
		}
		if (arg instanceof Event) {
			onEvent((Event)arg);
		}
	}
	
	private void onEvent(Event e) {
		if (e instanceof ProjectileHitEvent) {
			onProjectileHitEvent((ProjectileHitEvent)e);
		}
	}
	
	private void onProjectileHitEvent(ProjectileHitEvent event) {
		Block block = event.getHitBlock();
		if (block == null || !this.blocksPoints.containsKey(block.getType())) {
			return;
		}
		String team = getBlockTeam(block);
		if (!this.blocksPoints.containsKey(block.getType())) {
			return;
		}
		int points = this.blocksPoints.get(block.getType());
		Set<Score> scores = this.board.getScores(team);
		if (scores == null || scores.isEmpty()) {
			return;
		}
		Score score = scores.iterator().next();
		score.setScore(score.getScore() + points);
		spawnRandomBlock(team);
	}
	
	private String getBlockTeam(Block block) {
		Material material = block.getType();
		for (Entry<String, List<Material>> entry : this.teamColorsBlocks.entrySet()) {
			for (Material m : entry.getValue()) {
				if (material == m) {
					return entry.getKey();
				}
			}
		}
		return null;
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
		List<Material> materials = this.teamColorsBlocks.get(team);
		
		if (materials == null) {
			return null;
		}
		
		int length = materials.size();
		
		int randomIndex = rnd.nextInt(length);
		return materials.get(randomIndex);
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
		
		List<String> teams = getWinningTeamsByOrder();
		
		int currentPlace = 1;
		
		for (String team : teams) {
			
			giveRewardsToTeam(team, currentPlace);
			
		}
		
	}
	
	public void command(Player player, String[] args) {
		if (args.length <= 0) {
			return;
		}
		
		String command = args[0];
		
		if (command.equalsIgnoreCase("addblock")) {
			if (args.length < 5 || args.length > 6) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] addblock [TeamID] [Points] {Block}");
				return;
			}
			try {
				int points = Integer.parseInt(args[4]);
				String block = args.length == 6 ? args[5] : null;
				addBlock(player, args[3], block, points);
			} catch (NumberFormatException e) {
				player.sendMessage("Points should be an integer!");
				return;
			}
		}
	}
	
	private void addBlock(Player player, String teamID, String block, int points) {
		
		if (!player.hasPermission("drminigames.drcolorshooting.addblock." + this.arena.getID())) {
			player.sendMessage("You don't have permission to run this command! (drminigames.drcolorshooting.addblock." + this.arena.getID() + ")");
			return;
		}
		
		if (!this.teamColors.contains(teamID)) {
			player.sendMessage("Team " + teamID + " doesn't exist!");
		}
		
		Material material = null;
		
		if (block == null) {
			
			ItemStack holdingItem = player.getInventory().getItemInMainHand();
			
			if (holdingItem == null) {
				player.sendMessage("You didn't type block type nor hold a block in your main hand!");
				return;
			}
			
			material = holdingItem.getType();
			
			if (!material.isBlock()) {
				player.sendMessage("You didn't hold a block type item!");
				return;
			}
			
		} else {
			
			material = Material.matchMaterial(block);
			
			if (material == null) {
				player.sendMessage("Block not found!");
				return;
			}
			
		}
		
		this.teamColorsBlocks.get(teamID).add(material);
		this.blocksPoints.put(material, points);
	}
		
	private boolean spawnRandomBlock(String team) {
		
		Material randomMaterial = getRandomMaterialOfTeam(team);
		
		if (randomMaterial == null) {
			return false;
		}
		
		Region region = this.arena.getRegions().get(team);
		
		if (region == null) {
			return false;
		}
		
		List<Location> locations = RegionUtils.getRandomNBlocksInRegion(region, 1, (Location location)->location.getBlock().getType() == Material.AIR);
		
		if (locations == null || locations.isEmpty()) {
			return false;
		}
		
		locations.get(0).getBlock().setType(randomMaterial);
		
		return true;
		
	}
	
	private Scoreboard initializeScoreboard(List<String> teams) {
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		
		Scoreboard scoreboard = manager.getNewScoreboard();
		
		for (String teamID : teams) {
			
			Team team = scoreboard.registerNewTeam(teamID);
			
			String prefix;
			
			if (this.teamColorsPrefixes.containsKey(teamID)) {
				
				prefix = ChatColor.translateAlternateColorCodes('&', this.teamColorsPrefixes.get(teamID));
				
			} else {
				
				prefix = "[" + teamID + "]";
				
			}
			
			team.setPrefix(prefix);
			
			team.setDisplayName(teamID);
			
			team.setCanSeeFriendlyInvisibles(true);
			
			team.setAllowFriendlyFire(false);
			
		}
		
		Objective objective = scoreboard.registerNewObjective("showscores", "scores");
		
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		objective.setDisplayName("Scores:");
		
		for (String teamID : teams) {
			
			Score score = objective.getScore(teamID);
			
			score.setScore(0);
			
		}
		
		return scoreboard;
		
	}
	
	private void setSidebarScoreboardToPlayers() {
		
		List<UUID> uuids = this.arena.getPlayers();
		
		for (UUID uuid : uuids) {
			
			Player player = Bukkit.getPlayer(uuid);
			
			if (player != null) {
				
				player.setScoreboard(this.board);
				
			}
			
		}
		
	}
	
	private List<String> getWinningTeamsByOrder() {
		
		List<String> teams = new ArrayList<String>();
		
		for (String team : this.teamColors) {
			
			teams.add(team);
			
		}
		
		teams.sort((String team1, String team2) -> {
			
			Set<Score> team1Scores = this.board.getScores(team1);
			Set<Score> team2Scores = this.board.getScores(team2);
			
			if (team1Scores == null || team1Scores.isEmpty() || team2Scores == null || team2Scores.isEmpty()) {
				
				return 1;
				
			}
			
			Score team1Score = team1Scores.iterator().next();
			Score team2Score = team2Scores.iterator().next();
			
			return team1Score.getScore() - team2Score.getScore();
			
		});
		
		return teams;
		
	}
	
	private void giveRewardsToTeam(String team, int place) {

		Entry<Integer, List<String>> teamRewardsEntry = this.rewardsCommands.floorEntry(place);
		
		if (teamRewardsEntry == null) {
			
			return;
			
		}
		
		List<String> teamRewards = teamRewardsEntry.getValue();
		
		if (teamRewards == null) {
			
			return;
			
		}
		
		Map<String, List<UUID>> playersTeamsMap = Multimaps.asMap(Multimaps.invertFrom(Multimaps.forMap(this.playersColors), ArrayListMultimap.create()));
		
		List<UUID> players = playersTeamsMap.get(team);
		
		if (players == null) {
			
			return;
			
		}
		
		for (UUID uuid : players) {
			
			Player player = Bukkit.getPlayer(uuid);
			
			if (player == null) {
				
				continue;
				
			}
			
			for (String command : teamRewards) {
				
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("<player>", player.getName()));
				
			}
			
		}
		

		
	}

}
