package com.danieli1818.drminigames.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;

public class BlockInformation {

	private Material material;
	private Byte data;
	private List<MetadataValue> metadata;
	
	public BlockInformation(@Nonnull Material material, Byte data, List<MetadataValue> metadata) {
		this.material = material;
		this.data = data;
		this.metadata = metadata;
	}
	
	public BlockInformation(@Nonnull Material material, Byte data) {
		this(material, data, new ArrayList<MetadataValue>());
	}
	
	public BlockInformation(@Nonnull Material material) {
		this(material, (Byte)null);
	}
	
	public BlockInformation(@Nonnull Material material, List<MetadataValue> metadata) {
		this(material, null, metadata);
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public Byte getData() {
		return this.data;
	}
	
	public List<MetadataValue> getMetadata() {
		return this.metadata;
	}
	
	public void setMaterial(@Nonnull Material material) {
		this.material = material;
	}
	
	public void setData(Byte data) {
		this.data = data;
	}
	
	public void setMetadata(@Nonnull List<MetadataValue> metadata) {
		this.metadata = metadata;
	}
	
	public boolean equals(Block block) {
		if (this.material != block.getType() || this.data != block.getData()) {
			return false;
		}
		//TODO change metadata to NBT data Of Lore and check if it's equal.
		return true;
	}
	
}
