package com.danieli1818.drminigames.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;

public class BlockInformation {

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
	
}
