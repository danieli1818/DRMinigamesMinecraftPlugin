package com.danieli1818.drminigames.resources.api;

public interface ArenaLogicFactory {

	public ArenaLogic create(Arena arena);
	
	public ArenaLogic create(Arena arena, String[] args);
	
}
