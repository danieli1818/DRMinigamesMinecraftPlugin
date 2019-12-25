package com.danieli1818.drminigames.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;

public class BlockInformation implements ConfigurationSerializable {

	private Material material;
	private byte data;
	
	public BlockInformation(@Nonnull Material material, byte data) {
		this.material = material;
		this.data = data;
	}
	
	public BlockInformation(@Nonnull MaterialData materialData) {
		this(materialData.getItemType(), materialData.getData());
	}
	
	public BlockInformation(@Nonnull Material material) {
		this(material, (byte)0);
	}
	
	public BlockInformation(@Nonnull Block block) {
		this(block.getType(), block.getData());
	}
	
	private BlockInformation() {
		this.material = null;
		this.data = 0;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public byte getData() {
		return this.data;
	}
	
	public void setMaterial(@Nonnull Material material) {
		this.material = material;
	}
	
	public void setData(byte data) {
		this.data = data;
	}
	
	public boolean equals(Block block) {
		if (this.material != block.getType() || this.data != block.getData()) {
			return false;
		}
		return true;
	}
	
	public void spawnBlockInLocation(@Nonnull Location location) {
		if (location == null) {
			return;
		}
		Block block = location.getBlock();
		block.setType(this.material);
		block.setData(this.data);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlockInformation)) {
			return false;
		}
		BlockInformation other = (BlockInformation)obj;
		return this.material == other.material && this.data == other.data;
	}
	
	@Override
	public int hashCode() {
		String value = String.valueOf(this.material.getId()) + "-" + String.valueOf(this.data);
		return value.hashCode();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("material", this.material.getId());
		map.put("data", this.data);
		return map;
	}
	
	public static BlockInformation deserialize(Map<String, Object> map) {
		BlockInformation blockInformation = new BlockInformation();
		if (map.containsKey("material")) {
			try {
				blockInformation.material = Material.getMaterial((Integer) map.get("material"));
				if (!blockInformation.material.isBlock()) {
					return null;
				}
				if (map.containsKey("data")) {
					blockInformation.data = ((Integer)map.get("data")).byteValue();
				}
				return blockInformation;
			} catch (NumberFormatException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
}
