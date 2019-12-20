package com.danieli1818.drminigames.common.configurationserializables.collections;

import java.util.Collection;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface ConfigurationSerializableCollection<E extends ConfigurationSerializable> extends ConfigurationSerializable, Collection<E> {

}
