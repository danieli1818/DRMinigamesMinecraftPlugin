package com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.bukkit.configuration.serialization.ConfigurationSerializable;
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

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.subcommands.SetCommands;
import com.danieli1818.drminigames.common.BlockInformation;
import com.danieli1818.drminigames.common.configurationserializables.Timer;
import com.danieli1818.drminigames.common.exceptions.ArgumentOutOfBoundsException;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.ArenasManager;
import com.danieli1818.drminigames.utils.RegionUtils;
import com.sk89q.worldedit.regions.Region;

public class DRColorShooting implements ArenaLogic {
	
	private Arena arena;
	private Map<String, List<BlockPointsInformation>> teamColorsBlocks;
	private int numOfBlocksPerTeam;
	private Thread thread;
	private volatile Boolean shouldStop;
	private final Object shouldStopLock = new Object();
	private Random rnd;
	private Map<String, String> teamColorsPrefixes;
	private Scoreboard board;
	private NavigableMap<Integer, List<String>> rewardsCommands;
	private SetCommands setCommands;
	private Timer timer;
	
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
		this.teamColorsBlocks = new HashMap<String, List<BlockPointsInformation>>();
		for (String team : teamColors) {
			this.teamColorsBlocks.put(team, new ArrayList<BlockPointsInformation>());
		}
		this.rnd = new Random();
		this.teamColorsPrefixes = new HashMap<String, String>();
		this.rewardsCommands = new TreeMap<Integer, List<String>>();
		this.board = initializeScoreboard(teamColors);
		this.shouldStop = false;
		this.numOfBlocksPerTeam = 10;
		this.setCommands = new SetCommands(this);
		this.timer = new Timer();
		this.timer.setTask((Long time) -> {
			onTimeUpdated(time);
		});
	}
	
	public DRColorShooting(Arena arena) {
		this.arena = arena;
		this.teamColorsBlocks = new HashMap<String, List<BlockPointsInformation>>();
		this.rnd = new Random();
		this.teamColorsPrefixes = new HashMap<String, String>();
		this.rewardsCommands = new TreeMap<Integer, List<String>>();
		this.board = initializeScoreboard(new ArrayList<String>());
		this.shouldStop = false;
		this.numOfBlocksPerTeam = 10;
		this.setCommands = new SetCommands(this);
		this.timer = new Timer();
		this.timer.setTask((Long time) -> {
			onTimeUpdated(time);
		});
	}

	@Override
	public void start(Arena arena) {
		synchronized(this.shouldStopLock) {
			this.shouldStop = false;
			this.shouldStopLock.notifyAll();
		}
		this.timer.start();
		int taskID = runSyncStartTasks();
		if (taskID == -1) {
			synchronized(this.shouldStopLock) {
				this.shouldStop = true;
				this.shouldStopLock.notifyAll();
			}
			System.err.println("Error Running Sync Tasks For Starting The Game!");
			finish();
		}
		synchronized(this.shouldStopLock) {
			while (!this.shouldStop) {
				try {
					this.shouldStopLock.wait();
				} catch (InterruptedException e) {
					// e.printStackTrace();
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
				System.out.println("Spawn Or Region Of Team: " + teamName + " Doesn't Exist!");
				System.out.println("Spawns:");
				for (String spawnName : spawns.keySet()) {
					System.out.println(spawnName);
				}
				System.out.println("Spawns Done!");
				System.out.println("Regions:");
				for (String regionName : regions.keySet()) {
					System.out.println(regionName);
				}
				System.out.println("Spawns Done!");
				return false;
			}
		}
		if (arena.getLimits() == null) {
			System.out.println("Limits Is Null!");
			return false;
		}
		return true;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("Update Function Has Been Called!");
		if (!this.arena.isRunning()) {
			return;
		}
		if (arg instanceof Event) {
			onEvent((Event)arg);
		}
	}
	
	private void onEvent(Event e) {
		System.out.println("OnEvent Function Has Been Called!");
		if (e instanceof ProjectileHitEvent) {
			onProjectileHitEvent((ProjectileHitEvent)e);
		}
	}
	
	private void onProjectileHitEvent(ProjectileHitEvent event) {
		Block block = event.getHitBlock();
		System.out.println("BlockPoints Contains Key Block Value Is: " + getTeamAndBlockPointsInformationOfBlock(block));
		if (block == null || getTeamAndBlockPointsInformationOfBlock(block) == null) {
			return;
		}
		Entry<String, BlockPointsInformation> entry = getTeamAndBlockPointsInformationOfBlock(block);
		String team = entry.getKey();
		System.out.println("Team is: " + team + "!");
//		if (!this.blocksPoints.containsKey(block.getType())) {
//			return;
//		}
		int points = entry.getValue().getPoints();
		Objective showScores = this.board.getObjective("showscores");
		if (showScores == null) {
			System.out.println("showscores Objective Is Null!");
			return;
		}
		Score score = showScores.getScore(team);
//		Set<Score> scores = this.board.getScores(team);
//		if (scores == null || scores.isEmpty()) {
//			return;
//		}
//		Score score = scores.iterator().next();
		if (score == null) {
			System.out.println("score Is Null!");
			return;
		}
		score.setScore(score.getScore() + points);
		block.setType(Material.AIR); // remove block.
		event.getEntity().remove(); // remove projectile.
		spawnRandomBlock(team); // spawn new block.
	}
	
	private Entry<String, BlockPointsInformation> getTeamAndBlockPointsInformationOfBlock(Block block) {
		for (Entry<String, List<BlockPointsInformation>> entry : this.teamColorsBlocks.entrySet()) {
			for (BlockPointsInformation blockPointsInformations : entry.getValue()) {
				if (blockPointsInformations != null && blockPointsInformations.equals(block)) {
					return new AbstractMap.SimpleEntry(entry.getKey(), entry.getValue());
				}
			}
		}
		return null;
	}
	
	private String getBlockTeam(Block block) {
		for (Entry<String, List<BlockPointsInformation>> entry : this.teamColorsBlocks.entrySet()) {
			for (BlockPointsInformation m : entry.getValue()) {
				if (m.equals(block)) {
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
			}
		}

		for (Entry<String, List<Location>> locations : locationsPerRegion.entrySet()) {
			for (Location location : locations.getValue()) {
				getRandomBlockInformationOfTeam(locations.getKey()).spawnBlockInLocation(location);
			}
		}

	}
	
	private BlockPointsInformation getRandomBlockInformationOfTeam(String team) {
		List<BlockPointsInformation> blockInformations = this.teamColorsBlocks.get(team);
		
		if (blockInformations == null) {
			return null;
		}
		
		int length = blockInformations.size();
		
		int randomIndex = rnd.nextInt(length);
		return blockInformations.get(randomIndex);
	}
	
	public boolean stop() {
		synchronized(this.shouldStopLock) {
			this.shouldStop = true;
			this.shouldStopLock.notifyAll();
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
		
		System.out.println("Finish Function Has Been Called!");
		
		stopTimer();
		
		List<Team> teams = getWinningTeamsByOrder();
		
		int currentPlace = 1;
		
		for (Team team : teams) {
			
			giveRewardsToTeam(team, currentPlace);
			
		}
		
		this.arena.finishGame();
		
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
		} else if (command.equalsIgnoreCase("set")) {
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
		
		MaterialData data = null;
		
		if (block == null) {
			
			ItemStack holdingItem = player.getInventory().getItemInMainHand();
			
			if (holdingItem == null) {
				player.sendMessage("You didn't type block type nor hold a block in your main hand!");
				return;
			}
			
			data = holdingItem.getData();
			
			if (!data.getItemType().isBlock()) {
				player.sendMessage("You didn't hold a block type item!");
				return;
			}
			
		} else {
			
			String[] materialIDSubID = block.split(":");
			
			Material material = null;
			if (materialIDSubID.length >= 1) {
				material = Material.matchMaterial(materialIDSubID[0]);
				
				if (material == null) {
					player.sendMessage("Block not found!");
					return;
				}
				
				if (!material.isBlock()) {
					player.sendMessage("Not Valid Block!");
					return;
				}
				
				if (material != null && materialIDSubID.length >= 2) {
					try {
						data = new MaterialData(material, Byte.parseByte(materialIDSubID[1]));
					} catch (NumberFormatException e) {
						player.sendMessage("Not Valid SubID!");
						data = new MaterialData(material);
					}
				} else {
					data = new MaterialData(material);
				}
			}
			

			
		}
		
		BlockPointsInformation bi = new BlockPointsInformation(new BlockInformation(data), points);
		this.teamColorsBlocks.get(teamID).add(bi);
	}
		
	private boolean spawnRandomBlock(String team) {
		
		BlockPointsInformation randomBlockInformation = getRandomBlockInformationOfTeam(team);
		
		if (randomBlockInformation == null) {
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
		
		randomBlockInformation.spawnBlockInLocation(locations.get(0));
		
		return true;
		
	}
	
	private Scoreboard initializeScoreboard(Collection<String> teams) {
		
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
		this.teamColorsBlocks.put(name, new ArrayList<BlockPointsInformation>());
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
	
	public void setNumOfBlocksPerTeam(int num) throws ArgumentOutOfBoundsException {
		if (num <= 0) {
			throw new ArgumentOutOfBoundsException();
		}
		this.numOfBlocksPerTeam = num;
	}
	
	private int runSyncStartTasks() {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(DRMinigames.getPlugin(DRMinigames.class), new Runnable() {
			@Override
			public void run() {
				setCurrentThread();
				preStartInitialize();
				setTeamsToPlayers();
				teleportPlayersToArena();
				spawnRandomTeamBlocks();
				setSidebarScoreboardToPlayers();
				
			}
		});
	}
	
	public void setTimeForGame(long time) throws ArgumentOutOfBoundsException {
		if (time <= 0) {
			throw new ArgumentOutOfBoundsException();
		}
		this.timer.setTime(time);
	}
	
	private void stopTimer() {
		this.timer.stopTimer();
	}
	
	private void onTimeUpdated(final long time) {
		System.out.println("onTimeUpdated Function has been called with time: " + time);
		if (time <= 0) {
			stop();
		} else {
			final long timeInSecs = time / 1000;
			Bukkit.getScheduler().scheduleSyncDelayedTask(DRMinigames.getPlugin(DRMinigames.class), () -> {
				for (Team team : getTeams()) {
					for (OfflinePlayer player : team.getPlayers()) {
						Player p = player.getPlayer();
						if (p == null) {
							continue;
						}
						if (time > Integer.MAX_VALUE) {
							p.setTotalExperience(Integer.MAX_VALUE);
						}
						p.setTotalExperience((int)time);
					}
				}
			});

		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", arena.getID());
		map.put("teamBlocks", teamColorsBlocks);
		map.put("numOfBlocksPerTeam", numOfBlocksPerTeam);
		map.put("teamColorsPrefixes", teamColorsPrefixes);
		map.put("rewardsCommands", rewardsCommands.entrySet().stream().map((Entry<Integer, List<String>> entry) -> {
			return new AbstractMap.SimpleEntry<String, List<String>>(entry.getKey().toString(), entry.getValue());
		}).collect(Collectors.toMap((Entry::getKey), Entry::getValue)));
		return map;
	}
	
	public static DRColorShooting deserialize(Map<String, Object> map) {
		System.out.println("map:");
		for (String string : map.keySet()) {
			System.out.println(string);
		}
		System.out.println("done");
		Throwable t = new Throwable();
		t.printStackTrace();
		if (!map.containsKey("id")) {
			System.out.println("ID Doesn't Exist!");
			return null;
		}
		System.out.println("Is Arena Null: " + ArenasManager.getInstance().getArena((String)map.get("id")) == null);
		Arena arena = ArenasManager.getInstance().getArena((String)map.get("id"));
		if (arena == null) {
			System.out.println();
		}
		DRColorShooting arenaLogic = new DRColorShooting(ArenasManager.getInstance().getArena((String)map.get("id")));
		if (arenaLogic.arena == null) {
			System.out.println("Arena Is Null!");
			return null;
		}
		if (map.get("teamBlocks") != null && map.get("teamBlocks") instanceof Map<?, ?>) {
			arenaLogic.teamColorsBlocks = (Map<String, List<BlockPointsInformation>>)map.get("teamBlocks");
			arenaLogic.board = arenaLogic.initializeScoreboard(arenaLogic.teamColorsBlocks.keySet());
		}
		if (map.get("numOfBlocksPerTeam") != null && map.get("numOfBlocksPerTeam") instanceof Integer) {
			arenaLogic.numOfBlocksPerTeam = (Integer)map.get("numOfBlocksPerTeam");
		}
		if (map.get("teamColorsPrefixes") != null && map.get("teamColorsPrefixes") instanceof Map<?, ?>) {
			arenaLogic.teamColorsPrefixes = (Map<String, String>)map.get("teamColorsPrefixes");
		}
		if (map.get("rewardsCommands") != null && map.get("rewardsCommands") instanceof Map<?, ?>) {
			Map<String, List<String>> rewardsCommandsStringMap = (Map<String, List<String>>)map.get("rewardsCommands");
			arenaLogic.rewardsCommands = new TreeMap<>(rewardsCommandsStringMap.entrySet().stream().map((Entry<String, List<String>> entry) -> {
				return new AbstractMap.SimpleEntry<>(Integer.parseInt(entry.getKey()), entry.getValue());
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		}
		return arenaLogic;
	}
	
	public static class BlockPointsInformation implements ConfigurationSerializable {

		private BlockInformation blockInfo;
		private int points;
		
		public BlockPointsInformation(BlockInformation blockInfo, int points) {
			this.blockInfo = blockInfo;
			this.points = points;
		}
		
		public void spawnBlockInLocation(Location location) {
			blockInfo.spawnBlockInLocation(location);
		}

		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("blockInfo", this.blockInfo);
			map.put("points", this.points);
			return map;
		}
		
		public static BlockPointsInformation deserializable(Map<String, Object> map) {
			if (!map.containsKey("blockInfo") || !map.containsKey("points")) {
				return null;
			}
			Object o = map.get("blockInfo");
			if (o == null || !(o instanceof BlockInformation)) {
				return null;
			}
			BlockInformation blockInfo = (BlockInformation)o;
			try {
				int points = Integer.parseInt((String)map.get("points"));
				return new BlockPointsInformation(blockInfo, points);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		
		public boolean equals(Block block) {
			return this.blockInfo.equals(block);
		}
		
		public BlockInformation getBlockInformation() {
			return this.blockInfo;
		}
		
		public int getPoints() {
			return this.points;
		}
	}
		
}
