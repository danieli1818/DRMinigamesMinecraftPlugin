package com.danieli1818.drminigames.common.configurationserializables.maps;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.danieli1818.drminigames.common.configurationserializables.collections.ConfigurationSerializableCollection;

public interface ConfigurationSerializableMap<K extends ConfigurationSerializable, V extends ConfigurationSerializable> extends ConfigurationSerializable, Map<K, V> {

}
