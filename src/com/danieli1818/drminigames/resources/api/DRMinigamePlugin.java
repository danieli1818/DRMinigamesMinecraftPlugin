package com.danieli1818.drminigames.resources.api;

public interface DRMinigamePlugin {

	public ArenaLogicFactory getArenaLogicFactory();
	
	public void registerSerializableClasses();
	
	public void unregisterSerializableClasses();
	
	public String getID();
	
}
