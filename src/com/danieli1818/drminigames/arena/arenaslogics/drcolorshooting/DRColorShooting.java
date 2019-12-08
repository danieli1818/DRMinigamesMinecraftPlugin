package com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
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
import org.bukkit.OfflinePlayer;
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

import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.subcommands.SetCommands;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.RegionUtils;
import com.sk89q.worldedit.regions.Region;

public class DRColorShooting implements ArenaLogic {
	
	private Arena arena;
	private Map<String, List<Material>> teamColorsBlocks;
	private Map<Material, Integer> blocksPoints;
	private int numOfBlocksPerTeam;
	private Thread thread;
	private volatile Boolean shouldStop;
	private Random rnd;
	private Map<String, String> teamColorsPrefixes;
	private Scoreboard board;
	private NavigableMap<Integer, List<String>> rewardsCommands;
	private SetCommands setCommands;
	
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
		this.teamColorsBlocks = new HashMap<String, List<Material>>();
		for (String team : teamColors) {
			this.teamColorsBlocks.put(team, new ArrayList<Material>());
		}
		this.blocksPoints = new HashMap<Material, Integer>();
		this.rnd = new Random();
		this.teamColorsPrefixes = new HashMap<String, String>();
		this.rewardsCommands = new TreeMap<Integer, List<String>>();
		this.board = initializeScoreboard(teamColors);
		this.shouldStop = false;
		this.numOfBlocksPerTeam = 10;
		this.setCommands = new SetCommands(this);
	}
	
	public DRColorShooting(Arena arena) {
		this.arena = arena;
		this.teamColorsBlocks = new HashMap<String, List<Material>>();
		this.blocksPoints = new HashMap<Material, Integer>();
		this.rnd = new Random();
		this.teamColorsPrefixes = new HashMap<String, String>();
		this.rewardsCommands = new TreeMap<Integer, List<String>>();
		this.board = initializeScoreboard(new ArrayList<String>());
		this.shouldStop = false;
		this.numOfBlocksPerTeam = 10;
		this.setCommands = new SetCommands(this);
	}

	@Override
	public void start(Arena arena) {
		synchronized(this.shouldStop) {
			this.shouldStop = false;
			this.shouldStop.notifyAll();
		}
		setCurrentThread();
		preStartInitialize();
		setTeamsToPlayers();
		teleportPlayersToArena();
		spawnRandomTeamBlocks();
		setSidebarScoreboardToPlayers();
		synchronized(this.shouldStop) {
			if (!this.shouldStop) {
				while (!this.shouldStop) {
					try {
						this.shouldStop.wait();
					} catch (InterruptedException e) {
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
		for (Team team : getTeams()) {
			String teamName = team.getName();
			if (spawns.get(teamName) == null || regions.get(teamName) == null) {
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
		Set<Team> teams = this.board.getTeams();
		Iterator<Team> currentTeamColor = teams.iterator();
		for (UUID uuid : uuids) {
			if (!currentTeamColor.hasNext()) {
				currentTeamColor = teams.iterator();
			}
			currentTeamColor.next().addPlayer(Bukkit.getOfflinePlayer(uuid));
		}
	}
	
	private void teleportPlayersToArena() {
		Server server = Bukkit.getServer();
		Map<String, Location> spawns = this.arena.getSpawnLocation();
		for (Team team : getTeams()) {
			for (OfflinePlayer offlinePlayer : team.getPlayers()) {
				Player player = server.getPlayer(offlinePlayer.getUniqueId());
				if (player != null) {
					player.teleport(spawns.get(team.getName()));
				}
			}
		}
	}
	
	private void spawnRandomTeamBlocks() {
		Map<String, Region> regions = this.arena.getRegions();
		Map<String, List<Location>> locationsPerRegion = new HashMap<String, List<Location>>();
		for (Team team : getTeams()) {
			
			String teamName = team.getName();
			
			List<Location> locations = RegionUtils.getRandomNBlocksInRegion(regions.get(teamName), this.numOfBlocksPerTeam, (Location location) -> {
				Block block = location.getBlock();
				return block == null || block.getType() == Material.AIR;
			});
			
			if (locations != null) {
				locationsPerRegion.put(teamName, locations);
				System.out.println("Locations Aren't Null!");
				if (locations.isEmpty()) {
					System.out.println("Locations List Is Empty!");
				}
			} else {
				System.out.println("Locations Are Null!");
			}
			
		}

		for (Entry<String, List<Location>> locations : locationsPerRegion.entrySet()) {
			for (Location location : locations.getValue()) {
				location.getBlock().setType(getRandomMaterialOfTeam(locations.getKey()));
				System.out.println("Added Block!");
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
		
		List<Team> teams = getWinningTeamsByOrder();
		
		int currentPlace = 1;
		
		for (Team team : teams) {
			
			giveRewardsToTeam(team, currentPlace);
			
		}
		
		reset();
		
	}
	
	public void command(Player player, String[] args) {
		if (args.length <= 0) {
			player.sendMessage("Command Not Found! Use /drminigames command [ArenaID] help for help!");
			return;
		}
		
		String command = args[0];
		
		if (command.equalsIgnoreCase("addblock")) {
			if (args.length < 3 || args.length > 4) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] addblock [TeamID] [Points] {Block}");
				return;
			}
			try {
				int points = Integer.parseInt(args[2]);
				String block = args.length == 4 ? args[3] : null;
				addBlock(player, args[1], block, points);
				player.sendMessage("Successfully Add Block!");
			} catch (NumberFormatException e) {
				player.sendMessage("Points should be an integer!");
				return;
			}
		} else if (command.equalsIgnoreCase("setprefix")) {
			if (args.length < 2) {
				this.setCommands.commands(player, null, new String[0]);
				return;
			}
			String subCommand = args[1];
			String[] arguments = Arrays.copyOfRange(args, 1, args.length);
			this.setCommands.commands(player, subCommand, arguments);
			return;
		} else if (command.equalsIgnoreCase("addteams")) {
			if (args.length < 2) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] addteams [TeamID1] [TeamID2] [TeamID3] [TeamID4] ...");
				return;
			}
			for (int i = 1; i < args.length; i++) {
				if (!addTeam(args[i])) {
					player.sendMessage("Team " + args[i] + " already exists!");
					continue;
				}
				player.sendMessage("Team " + args[i] + " has been added successfully!");
			}
			
			player.sendMessage("Successfully Add Team " + args[1] + "!");
		} else if (command.equalsIgnoreCase("removeteams")) {
			if (args.length < 2) {
				player.sendMessage("Invalid Syntax! Correct Syntax is: /drminigames command [ArenaID] removeteams [TeamID1] [TeamID2] [TeamID3] [TeamID4] ...");
				return;
			}
			for (int i = 1; i < args.length; i++) {
				if (removeTeam(args[i])) {
					this.board.getTeam(args[i]).unregister();
					player.sendMessage("Successfully Removed Team " + args[i] + "!");
				} else {
					player.sendMessage("Team " + args[i] + " Didn't Exist!");
				}
				continue;
			}
		} else {
			player.sendMessage("Command doesn't exist! Use /drminigame command [ArenaID] help for help!");
		}
	}
	
	private void addBlock(Player player, String teamID, String block, int points) {
		
		if (!player.hasPermission("drminigames.drcolorshooting.addblock." + this.arena.getID())) {
			player.sendMessage("You don't have permission to run this command! (drminigames.drcolorshooting.addblock." + this.arena.getID() + ")");
			return;
		}
		
		if (!containsTeam(teamID)) {
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
		
		Objective showScoresObjective = scoreboard.registerNewObjective("showscores", "dummy");
		
		showScoresObjective.setDisplayName("Scores:");
		
		showScoresObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
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
	
	private List<Team> getWinningTeamsByOrder() {
		
		List<Team> teams = new ArrayList<Team>();
		
		for (Team team : getTeams()) {
			
			teams.add(team);
			
		}
		
		teams.sort((Team team1, Team team2) -> {
			
			Set<Score> team1Scores = this.board.getScores(team1.getName());
			Set<Score> team2Scores = this.board.getScores(team2.getName());
			
			if (team1Scores == null || team1Scores.isEmpty() || team2Scores == null || team2Scores.isEmpty()) {
				
				return 1;
				
			}
			
			Score team1Score = team1Scores.iterator().next();
			Score team2Score = team2Scores.iterator().next();
			
			return team1Score.getScore() - team2Score.getScore();
			
		});
		
		return teams;
		
	}
	
	private void giveRewardsToTeam(Team team, int place) {

		Entry<Integer, List<String>> teamRewardsEntry = this.rewardsCommands.floorEntry(place);
		
		if (teamRewardsEntry == null) {
			return;
		}
		
		List<String> teamRewards = teamRewardsEntry.getValue();
		
		if (teamRewards == null) {
			return;
		}
		
		Set<OfflinePlayer> players = team.getPlayers();
		
		if (players == null) {
			return;
		}
		
		for (OfflinePlayer offlinePlayer : players) {
			
			Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
			
			if (player == null) {
				continue;
			}
			
			for (String command : teamRewards) {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("<player>", player.getName()));
			}
			
		}
		
	}
	
	public void reset() {
		
		if (this.board.getObjective("showscores") == null) {
			this.board = initializeScoreboard(new ArrayList<String>());
		}
		
		for (String entry : this.board.getEntries()) {
			this.board.resetScores(entry);
		}
		
	}
	
	private void preStartInitialize() {
		
		Objective showScores = this.board.getObjective("showscores");
		
		for (Team team : this.board.getTeams()) {
			Score score = showScores.getScore(team.getName());
			score.setScore(0);
		}
		
	}
	
	private Set<Team> getTeams() {
		if (this.board == null) {
			this.board = initializeScoreboard(new ArrayList<String>());
		}
		return this.board.getTeams();
	}
	
	public boolean containsTeam(String name) {
		return this.board.getTeam(name) != null;
	}
	
	private boolean addTeam(String name) {
		if (containsTeam(name)) {
			return false;
		}
		this.board.registerNewTeam(name);
		this.teamColorsBlocks.put(name, new ArrayList<Material>());
		return true;
	}
	
	private boolean removeTeam(String name) {
		if (!containsTeam(name)) {
			return false;
		}
		this.board.getTeam(name).unregister();
		this.teamColorsBlocks.remove(name);
		return true;
	}
	
	public boolean addPrefixToTeam(String teamID, String prefix) {
		boolean returnValue = false;
		if (this.teamColorsPrefixes.containsKey(teamID)) {
			returnValue = true;
		}
		this.teamColorsPrefixes.put(teamID, prefix);
		return returnValue;
	}
	
	public void setNumOfBlocksPerTeam(int num) throws NumberFormatException {
		if (num <= 0) {
			throw new NumberFormatException();
		}
		this.numOfBlocksPerTeam = num;
	}

}
