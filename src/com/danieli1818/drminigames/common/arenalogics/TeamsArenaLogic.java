package com.danieli1818.drminigames.common.arenalogics;

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
import java.util.NavigableMap;
import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.danieli1818.drminigames.DRMinigames;
import com.danieli1818.drminigames.common.arenalogics.subcommands.AddCommands;
import com.danieli1818.drminigames.common.arenalogics.subcommands.RemoveCommands;
import com.danieli1818.drminigames.common.arenalogics.subcommands.SetCommands;
import com.danieli1818.drminigames.common.configurationserializables.Timer;
import com.danieli1818.drminigames.common.exceptions.ArgumentOutOfBoundsException;
import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogic;
import com.danieli1818.drminigames.utils.ArenasManager;
import com.sk89q.worldedit.regions.Region;

public abstract class TeamsArenaLogic implements ArenaLogic {

	private Arena arena;
	private Thread thread;
	private volatile Boolean shouldStop;
	private final Object shouldStopLock = new Object();
	private Map<String, String> teamPrefixes;
	private Scoreboard board;
	private NavigableMap<Integer, List<String>> rewardsCommands;
	private SetCommands setCommands;
	private Timer timer;
	private AddCommands addCommands;
	private RemoveCommands removeCommands;
	
//		private class TeamColorBlock {
//			
//			private Block block;
//			private int points;
//			
//			public TeamColorBlock(Block block, int points) {
//				this.block = block;
//				this.points = points;
//			}
//			
//		}
	
	public TeamsArenaLogic(Arena arena, String[] args) {
		this(arena, Arrays.asList(args));
	}
	
	public TeamsArenaLogic(Arena arena, List<String> teams) {
		this.arena = arena;
		this.teamPrefixes = new HashMap<String, String>();
		this.rewardsCommands = new TreeMap<Integer, List<String>>();
		this.board = initializeScoreboard(teams);
		this.shouldStop = false;
		this.setCommands = new SetCommands(this);
		this.timer = new Timer();
		this.timer.setTask((Long time) -> {
			onTimeUpdated(time);
		});
		this.addCommands = new AddCommands(this);
		this.removeCommands = new RemoveCommands(this);
	}
	
	public TeamsArenaLogic(Arena arena) {
		this.arena = arena;
		this.teamPrefixes = new HashMap<String, String>();
		this.rewardsCommands = new TreeMap<Integer, List<String>>();
		this.board = initializeScoreboard(new ArrayList<String>());
		this.shouldStop = false;
		this.setCommands = new SetCommands(this);
		this.timer = new Timer();
		this.timer.setTask((Long time) -> {
			onTimeUpdated(time);
		});
		this.addCommands = new AddCommands(this);
		this.removeCommands = new RemoveCommands(this);
	}

	@Override
	public void start(Arena arena) {
		synchronized(this.shouldStopLock) {
			this.shouldStop = false;
			this.shouldStopLock.notifyAll();
		}
		onStart();
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
		if (!this.arena.isRunning()) {
			return;
		}
	}
	
	public String getArenaID() {
		return this.arena.getID();
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
			Team team = currentTeamColor.next();
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			team.addPlayer(offlinePlayer);
			Player player = offlinePlayer.getPlayer();
			player.sendMessage("Joined " + team.getDisplayName() + "!");
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
		
		stopTimer();
		
		List<Team> teams = getWinningTeamsByOrder();
		
		for (UUID uuid : this.arena.getPlayers()) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			offlinePlayer.getPlayer().sendMessage(getFinishPlayerMessages(offlinePlayer, teams));
		}
		
		int currentPlace = 1;
		
		for (Team team : teams) {
			
			giveRewardsToTeam(team, currentPlace);
			
		}
		
		this.arena.finishGame();
		
		reset();
		
	}
	
	public String[] getFinishPlayerMessages(OfflinePlayer player, List<Team> teams) {
		
		String[] messages = new String[7];
		messages[0] = "Results:";
		int place = 1;
		for (Team team : teams) {
			if (place == 5) {
				break;
			}
			messages[place] = place + ": " + team.getDisplayName(); 
			place++;
		}
		messages[place] = "";
		int currentPlayerTeamPlace = 1;
		for (Team team : teams) {
			if (team.hasPlayer(player)) {
				break;
			}
			currentPlayerTeamPlace++;
		}
		messages[place + 1] = "Your Team's Place Is: " + currentPlayerTeamPlace;
		messages = Arrays.copyOf(messages, place + 2);
		return messages;
		
	}
	
	public Team getPlayerTeam(OfflinePlayer player) {
		return this.board.getPlayerTeam(player);
	}
	
	public boolean command(Player player, String[] args) {
		if (args.length <= 0) {
			player.sendMessage("Command Not Found! Use /drminigames command [ArenaID] help for help!");
			return true;
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
			return false;
		}
	}
	
	private Scoreboard initializeScoreboard(Collection<String> teams) {
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		
		Scoreboard scoreboard = manager.getNewScoreboard();
		
		for (String teamID : teams) {
			
			Team team = scoreboard.registerNewTeam(teamID);
			
			String prefix;
			
			if (this.teamPrefixes.containsKey(teamID)) {
				
				prefix = ChatColor.translateAlternateColorCodes('&', this.teamPrefixes.get(teamID));
				
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
			
			return -team1Score.getScore() + team2Score.getScore();
			
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
	protected Set<Team> getTeams() {
		if (this.board == null) {
			this.board = initializeScoreboard(new ArrayList<String>());
		}
		return this.board.getTeams();
	}
	
	public boolean containsTeam(String name) {
		return this.board.getTeam(name) != null;
	}
	
	public boolean addTeam(String name) {
		if (containsTeam(name)) {
			return false;
		}
		this.board.registerNewTeam(name);
		return true;
	}
	
	public boolean removeTeam(String name) {
		if (!containsTeam(name)) {
			return false;
		}
		this.board.getTeam(name).unregister();
		return true;
	}
	
	public boolean addPrefixToTeam(String teamID, String prefix) {
		boolean returnValue = false;
		if (this.teamPrefixes.containsKey(teamID)) {
			returnValue = true;
		}
		this.teamPrefixes.put(teamID, prefix);
		return returnValue;
	}
	
	public void setNumOfBlocksPerTeam(int num) throws ArgumentOutOfBoundsException {
		if (num <= 0) {
			throw new ArgumentOutOfBoundsException();
		}
	}
	
	private int runSyncStartTasks() {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(DRMinigames.getPlugin(DRMinigames.class), new Runnable() {
			@Override
			public void run() {
				setCurrentThread();
				preStartInitialize();
				setTeamsToPlayers();
				teleportPlayersToArena();
				setSidebarScoreboardToPlayers();
				onSyncStart();
				
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
		map.put("teams", getTeamsNames());
		map.put("teamPrefixes", teamPrefixes);
		map.put("rewardsCommands", rewardsCommands.entrySet().stream().map((Entry<Integer, List<String>> entry) -> {
			return new AbstractMap.SimpleEntry<String, List<String>>(entry.getKey().toString(), entry.getValue());
		}).collect(Collectors.toMap((Entry::getKey), Entry::getValue)));
		return map;
	}
	
	public static TeamsArenaLogic deserialize(Map<String, Object> map, TeamsArenaLogicFactory teamsArenaLogicFactory) {
		if (map == null || !map.containsKey("id") || teamsArenaLogicFactory == null) {
			return null;
		}
		Arena arena = ArenasManager.getInstance().getArena((String)map.get("id"));
		TeamsArenaLogic arenaLogic = teamsArenaLogicFactory.create(arena);
		if (arenaLogic.arena == null) {
			return null;
		}
		if (map.get("teams") != null && map.get("teams") instanceof List<?>) {
			arenaLogic.board = arenaLogic.initializeScoreboard((List<String>)map.get("teams"));
		}
		if (map.get("teamPrefixes") != null && map.get("teamPrefixes") instanceof Map<?, ?>) {
			arenaLogic.teamPrefixes = (Map<String, String>)map.get("teamPrefixes");
		}
		if (map.get("rewardsCommands") != null && map.get("rewardsCommands") instanceof Map<?, ?>) {
			Map<String, List<String>> rewardsCommandsStringMap = (Map<String, List<String>>)map.get("rewardsCommands");
			arenaLogic.rewardsCommands = new TreeMap<>(rewardsCommandsStringMap.entrySet().stream().map((Entry<String, List<String>> entry) -> {
				return new AbstractMap.SimpleEntry<>(Integer.parseInt(entry.getKey()), entry.getValue());
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		}
		return arenaLogic;
	}

	@Override
	public String getID() {
		return "TeamsDRMinigame";
	}
	
	public void onStart() {}
	
	public void onSyncStart() {}
	
	protected boolean isRunning() {
		return this.arena.isRunning();
	}
	
	protected Integer addPointsToTeam(int points, String teamID) {
		
		Objective showScores = this.board.getObjective("showscores");
		if (showScores == null) {
			return null;
		}
		Score score = showScores.getScore(teamID);
		if (score == null) {
			return null;
		}
		score.setScore(score.getScore() + points);
		
		return score.getScore();
	}
	
	protected List<UUID> getPlayers() {
		return this.arena.getPlayers();
	}
	
	protected Map<String, Location> getSpawnLocations() {
		return this.arena.getSpawnLocation();
	}
	
	protected Map<String, Region> getRegions() {
		return this.arena.getRegions();
	}
	
	protected Set<Score> getScores(String teamID) {
		return this.board.getScores(teamID);
	}
	
	private List<String> getTeamsNames() {
		return getTeams().stream().map((Team team) -> team.getName()).collect(Collectors.toList());
	}
	
	public void addRewardCommand(int place, String command) {
		List<String> rewardsCommands = this.rewardsCommands.get(place);
		if (rewardsCommands == null) {
			rewardsCommands = new ArrayList<String>();
		}
		rewardsCommands.add(command);
		this.rewardsCommands.put(place, rewardsCommands);
	}
	
	public boolean removeRewardCommand(int place, String command) {
		List<String> rewardsCommands = this.rewardsCommands.get(place);
		if (rewardsCommands == null || !rewardsCommands.contains(command)) {
			return false;
		}
		rewardsCommands.remove(command);
		return true;
	}
	
	public void clearRewardsCommands(int place) {
		this.rewardsCommands.remove(place);
	}
	
}
