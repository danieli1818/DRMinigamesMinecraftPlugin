package com.danieli1818.drminigames.common.configurationserializables.collections.lists;

import java.util.List;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.danieli1818.drminigames.common.configurationserializables.collections.ConfigurationSerializableCollection;

public interface ConfigurationSerializableList<E extends ConfigurationSerializable> extends ConfigurationSerializableCollection<E>, List<E> {

}
