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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.subcommands.AddCommands;
import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.subcommands.RemoveCommands;
import com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting.subcommands.SetCommands;
import com.danieli1818.drminigames.common.BlockInformation;
import com.danieli1818.drminigames.common.arenalogics.TeamsArenaLogic;
import com.danieli1818.drminigames.common.configurationserializables.Timer;
import com.danieli1818.drminigames.common.exceptions.ArgumentOutOfBoundsException;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.ArenasManager;
import com.danieli1818.drminigames.utils.RegionUtils;
import com.sk89q.worldedit.regions.Region;

public class DRColorShooting extends TeamsArenaLogic implements ArenaLogic {
	
	private Map<String, List<BlockPointsInformation>> teamColorsBlocks;
	private int numOfBlocksPerTeam;
	private Random rnd;
	private SetCommands setCommands;
	private List<Location> spawnedBlocks;
	private AddCommands addCommands;
	private RemoveCommands removeCommands;
	
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
		super(arena, teamColors);
		this.teamColorsBlocks = new HashMap<String, List<BlockPointsInformation>>();
		for (String team : teamColors) {
			this.teamColorsBlocks.put(team, new ArrayList<BlockPointsInformation>());
		}
		this.rnd = new Random();
		this.numOfBlocksPerTeam = 10;
		this.setCommands = new SetCommands(this);
		this.addCommands = new AddCommands(this);
		this.removeCommands = new RemoveCommands(this);
	}
	
	public DRColorShooting(Arena arena) {
		super(arena);
		this.teamColorsBlocks = new HashMap<String, List<BlockPointsInformation>>();
		this.rnd = new Random();
		this.numOfBlocksPerTeam = 10;
		this.setCommands = new SetCommands(this);
		this.addCommands = new AddCommands(this);
		this.removeCommands = new RemoveCommands(this);
	}

	@Override
	public void start(Arena arena) {
		super.start(arena);
		
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
		if (!super.isRunning()) {
			return;
		}
		if (arg instanceof Event) {
			onEvent((Event)arg);
		}
	}
	
	private void onEvent(Event e) {
		if (e instanceof ProjectileHitEvent) {
			onProjectileHitEvent((ProjectileHitEvent)e);
		} else {
			if (e instanceof PlayerDropItemEvent) {
				onPlayerDropItemEvent((PlayerDropItemEvent)e);
			}
		}
	}
	
	private void onProjectileHitEvent(ProjectileHitEvent event) {
		Block block = event.getHitBlock();
		if (!isBlockInSpawnedBlocks(block) || getTeamAndBlockPointsInformationOfBlock(block) == null) {
			event.getEntity().remove();
			return;
		}
		Entry<String, BlockPointsInformation> entry = getTeamAndBlockPointsInformationOfBlock(block);
		String team = entry.getKey();
//		if (!this.blocksPoints.containsKey(block.getType())) {
//			return;
//		}
		int points = entry.getValue().getPoints();
		super.addPointsToTeam(points, team);
		block.setType(Material.AIR); // remove block.
		this.spawnedBlocks.remove(block.getLocation());
		event.getEntity().remove(); // remove projectile.
		spawnRandomBlock(team); // spawn new block.
	}
	
	private void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	private Entry<String, BlockPointsInformation> getTeamAndBlockPointsInformationOfBlock(Block block) {
		for (Entry<String, List<BlockPointsInformation>> entry : this.teamColorsBlocks.entrySet()) {
			for (BlockPointsInformation blockPointsInformations : entry.getValue()) {
				if (blockPointsInformations != null && blockPointsInformations.equals(block)) {
					return new AbstractMap.SimpleEntry(entry.getKey(), blockPointsInformations);
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
	
	public String getArenaID() {
		return super.getID();
	}
	
	private void setTeamsToPlayers() {
		List<UUID> uuids = super.getPlayers();
		Collections.shuffle(uuids);
		Set<Team> teams = super.getTeams();
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
		Map<String, Location> spawns = super.getSpawnLocations();
		for (Team team : getTeams()) {
			for (OfflinePlayer offlinePlayer : team.getPlayers()) {
				Player player = server.getPlayer(offlinePlayer.getUniqueId());
				if (player != null) {
					player.teleport(spawns.get(team.getName()));
				}
			}
		}
	}
	
	private List<Location> spawnRandomTeamBlocks() {
		Map<String, Region> regions = super.getRegions();
		Map<String, List<Location>> locationsPerRegion = new HashMap<String, List<Location>>();
		List<Location> allLocations = new ArrayList<Location>();
		for (Team team : getTeams()) {
			
			String teamName = team.getName();
			
			List<Location> locations = RegionUtils.getRandomNBlocksInRegion(regions.get(teamName), this.numOfBlocksPerTeam, (Location location) -> {
				Block block = location.getBlock();
				return block == null || block.getType() == Material.AIR;
			});
			
			if (locations != null) {
				locationsPerRegion.put(teamName, locations);
				allLocations.addAll(locations);
			}
		}

		for (Entry<String, List<Location>> locations : locationsPerRegion.entrySet()) {
			for (Location location : locations.getValue()) {
				getRandomBlockInformationOfTeam(locations.getKey()).spawnBlockInLocation(location);
			}
		}
		
		return allLocations;

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
	
	public boolean command(Player player, String[] args) {
		boolean flag = super.command(player, args);
		if (args.length <= 0) {
			return false;
		}
		
		String command = args[0];
		
		if (command.equalsIgnoreCase("add")) {
			if (args.length < 2) {
				return this.addCommands.commands(player, null, new String[0]);
			}
			String subCommand = args[1];
			String[] arguments = Arrays.copyOfRange(args, 1, args.length);
			return this.addCommands.commands(player, subCommand, arguments);
		} else if (command.equalsIgnoreCase("set")) {
			if (args.length < 2) {
				return this.setCommands.commands(player, null, new String[0]);
			}
			String subCommand = args[1];
			String[] arguments = Arrays.copyOfRange(args, 1, args.length);
			return this.setCommands.commands(player, subCommand, arguments);
		} else if (command.equalsIgnoreCase("remove")) {
			if (args.length < 2) {
				return this.removeCommands.commands(player, null, new String[0]);
			}
			String subCommand = args[1];
			String[] arguments = Arrays.copyOfRange(args, 1, args.length);
			return this.removeCommands.commands(player, subCommand, arguments);
		} else {
			if (!flag) {
				player.sendMessage("Command doesn't exist! Use /drminigame command [ArenaID] help for help!");
				return false;
			}
			return true;
		}
	}
	
	private boolean spawnRandomBlock(String team) {
		
		BlockPointsInformation randomBlockInformation = getRandomBlockInformationOfTeam(team);
		
		if (randomBlockInformation == null) {
			return false;
		}
		
		Region region = super.getRegions().get(team);
		
		if (region == null) {
			return false;
		}
		
		List<Location> locations = RegionUtils.getRandomNBlocksInRegion(region, 1, (Location location)->location.getBlock().getType() == Material.AIR);
		
		if (locations == null || locations.isEmpty()) {
			return false;
		}
		
		randomBlockInformation.spawnBlockInLocation(locations.get(0));
		
		this.spawnedBlocks.add(locations.get(0));
		
		return true;
		
	}
	
	private List<Team> getWinningTeamsByOrder() {
		
		List<Team> teams = new ArrayList<Team>();
		
		for (Team team : getTeams()) {
			
			teams.add(team);
			
		}
		
		teams.sort((Team team1, Team team2) -> {
			
			Set<Score> team1Scores = getScores(team1.getName());
			Set<Score> team2Scores = getScores(team2.getName());
			
			if (team1Scores == null || team1Scores.isEmpty() || team2Scores == null || team2Scores.isEmpty()) {
				
				return 1;
				
			}
			
			Score team1Score = team1Scores.iterator().next();
			Score team2Score = team2Scores.iterator().next();
			
			return team1Score.getScore() - team2Score.getScore();
			
		});
		
		return teams;
		
	}
		
	public void reset() {
		
		resetSynchronized();
		
	}
	
	private void resetSynchronized() {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(DRMinigames.getPlugin(DRMinigames.class), () -> {
			for (Location location : this.spawnedBlocks) {
				location.getBlock().setType(Material.AIR);
			}
		});

		
	}
	
	public void setNumOfBlocksPerTeam(int num) throws ArgumentOutOfBoundsException {
		if (num <= 0) {
			throw new ArgumentOutOfBoundsException();
		}
		this.numOfBlocksPerTeam = num;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("teamBlocks", teamColorsBlocks);
		map.put("numOfBlocksPerTeam", numOfBlocksPerTeam);
		return map;
	}
	
	public static DRColorShooting deserialize(Map<String, Object> map) {
		DRColorShooting arenaLogic = (DRColorShooting)TeamsArenaLogic.deserialize(map, new DRColorShootingFactory());
		if (arenaLogic == null) {
			return null;
		}
		if (map.get("teamBlocks") != null && map.get("teamBlocks") instanceof Map<?, ?>) {
			arenaLogic.teamColorsBlocks = (Map<String, List<BlockPointsInformation>>)map.get("teamBlocks");
		}
		if (map.get("numOfBlocksPerTeam") != null && map.get("numOfBlocksPerTeam") instanceof Integer) {
			arenaLogic.numOfBlocksPerTeam = (Integer)map.get("numOfBlocksPerTeam");
		}
		return arenaLogic;
	}
	
	public void addBlockPointsInformationToTeam(BlockPointsInformation bpi, String teamID) {
		this.teamColorsBlocks.get(teamID).add(bpi);
	}
	
	@Override
	public void onSyncStart() {
		super.onSyncStart();
		spawnedBlocks = spawnRandomTeamBlocks();
	}
	
	private boolean isBlockInSpawnedBlocks(Block block) {
		if (block == null) {
			return false;
		}
		return this.spawnedBlocks.contains(block.getLocation());
	}
		
}
