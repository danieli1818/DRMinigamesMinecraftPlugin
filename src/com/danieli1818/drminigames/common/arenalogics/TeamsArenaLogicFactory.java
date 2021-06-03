package com.danieli1818.drminigames.common.arenalogics;

import com.danieli1818.drminigames.resources.api.Arena;
import com.danieli1818.drminigames.resources.api.ArenaLogicFactory;

public interface TeamsArenaLogicFactory extends ArenaLogicFactory {

	public TeamsArenaLogic create(Arena arena);
	
}
