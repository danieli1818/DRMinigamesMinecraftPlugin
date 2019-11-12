package com.danieli1818.drminigames.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.regions.Region;

import net.minecraft.server.v1_12_R1.Blocks;

public class RegionUtils {

 	public static List<Location> getRandomNBlocksInRegion(Region region, int num, Predicate<Location> predicate) {
 		if (num < 0) {
 			return null;
 		}
 		if (num == 0) {
 			return new ArrayList<Location>();
 		}
 		World world = Bukkit.getWorld(region.getWorld().getName());
 		if (region.getArea() <= num) {
 			num = region.getArea();
 			if (predicate == null) {
 	 			List<Location> locations = new ArrayList<Location>();
 	 			for (BlockVector vector : region) {
 	 				locations.add(new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
 	 			}
 	 			return locations;
 			}
 		}
 		List<Location> blocks = new ArrayList<Location>();
 		int length = 0;
 		for (BlockVector vector : region) {
 			Location l = new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
 			if (predicate == null || predicate.test(l)) {
 				blocks.add(l);
 				length++;
 			}
 		}
 		Collections.shuffle(blocks);
 		List<Location> locations = new ArrayList<Location>();
 		if (length < num) {
 			num = length;
 		}
 		length = 0;
 		for (Location location : blocks) {
 			if (length == num) {
 				break;
 			}
 			locations.add(location);
 			length++;
 		}
 		return locations;
 	}
	
}
