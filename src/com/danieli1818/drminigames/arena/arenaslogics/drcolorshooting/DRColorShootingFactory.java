package com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting;

import com.danieli1818.drminigames.common.arenalogics.TeamsArenaLogic;
import com.danieli1818.drminigames.common.arenalogics.TeamsArenaLogicFactory;
import com.danieli1818.drminigames.resources.api.Arena;

public class DRColorShootingFactory implements TeamsArenaLogicFactory {

	@Override
	public TeamsArenaLogic create(Arena arena) {
		return new DRColorShooting(arena);
	}

	
	
}
