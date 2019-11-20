package com.danieli1818.drminigames.resources.api;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public interface ArenaLogic extends Observer {

	public void start(Arena arena);
	
//	public void onPlayerJoined(Player p);
	
	public boolean canBeAvailable(Arena arena);
	
	public String getID();
	
	public void loadArenaLogicFromMap(Map<String, String> map);
	
	public Map<String, String> getArenaLogicMap();
	
	@Override
	default void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean stop();
	
	public void forceStop();
	
}
