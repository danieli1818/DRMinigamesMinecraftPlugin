package com.danieli1818.drminigames.arena.arenaslogics.drcolorshooting;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.danieli1818.drminigames.common.BlockInformation;

public class BlockPointsInformation implements ConfigurationSerializable {

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
	
	public static BlockPointsInformation deserialize(Map<String, Object> map) {
		if (!map.containsKey("blockInfo") || !map.containsKey("points")) {
			return null;
		}
		Object o = map.get("blockInfo");
		if (o == null || !(o instanceof BlockInformation)) {
			return null;
		}
		BlockInformation blockInfo = (BlockInformation)o;
		try {
			int points;
			if (map.get("points") instanceof Byte) {
				points = (Byte)map.get("points");
			} else if (map.get("points") instanceof Integer) {
				points = (Integer)map.get("points");
			} else {
				return null;
			}
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
