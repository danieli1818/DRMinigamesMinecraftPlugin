package com.danieli1818.drminigames.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;


public class TextScoreboard {
	
	private Scoreboard scoreboard;
	
	private Objective objective;
	
	private List<String> lines;
	
	private final static ScoreboardManager manager = Bukkit.getScoreboardManager();
	
	public TextScoreboard() {
		this.scoreboard = manager.getNewScoreboard();
		this.objective = this.scoreboard.registerNewObjective("main", "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.lines = new ArrayList<String>(16);
		for (int i = 0; i < 16; i++) {
			this.lines.add(null);
		}
	}
	
	public TextScoreboard(String title) {
		this();
		this.objective.setDisplayName(title);
	}
	
	public TextScoreboard(String title, List<String> lines) {
		this(title);
		int num = Math.min(lines.size(), 16);
		for (int i = 0; i < num; i++) {
			this.lines.set(i, lines.get(i));
		}
		update();
	}
	
	public TextScoreboard(String title, String[] lines) {
		this(title, Arrays.asList(lines));
	}
	
	public TextScoreboard setLine(int lineNumber, String line) {
		this.lines.set(16 - lineNumber, line);
		update(lineNumber);
		return this;
	}
	
	private void update() {
		resetScoreboard();
		int index = 0;
		for (String line : this.lines) {
			if (line == null) {
				continue;
			}
			Score score = this.objective.getScore(line);
			score.setScore(16 - index);
			index++;
		}
	}
	
	private void update(int lineNumber) {
		
		String lineEntry = getLine(lineNumber);
		
		if (lineEntry != null) {
			System.out.println("lineEntry is null!");
			this.scoreboard.resetScores(lineEntry);
		} else {
			System.out.println("lineEntry is: " + lineEntry);
		}
		
		String line = this.lines.get(16 - lineNumber);
		
		if (line == null) {
			return;
		}
		
		Score score = this.objective.getScore(this.lines.get(16 - lineNumber));
		
		score.setScore(16 - lineNumber + 1);
		
	}
	
	private void resetScoreboard() {
		for (String entry : this.scoreboard.getEntries()) {
			this.scoreboard.resetScores(entry);
		}
	}
	
	private String getLine(int number) {
		number = 16 - number + 1;
		for (String entry : this.scoreboard.getEntries()) {
			Score score = this.objective.getScore(entry);
			int points = score.getScore();
			System.out.println("Entry: " + entry + " points: " + points);
			if (points == number) {
				return entry;
			}
		}
		
		return null;
	}
	
	public void setScoreboardToPlayer(Player player) {
		player.setScoreboard(this.scoreboard);
	}
	
	public Team registerNewTeam(String name) {
		return this.scoreboard.registerNewTeam(name);
	}
	
	public void unregisterTeam(String name) {
		Team team = getTeam(name);
		if (team != null) {
			team.unregister();
		}
	}
	
	public Team getTeam(String name) {
		return this.scoreboard.getTeam(name);
	}
	
	public void removeLine(int lineNumber) {
		this.lines.set(16 - lineNumber + 1, null);
	}

}
