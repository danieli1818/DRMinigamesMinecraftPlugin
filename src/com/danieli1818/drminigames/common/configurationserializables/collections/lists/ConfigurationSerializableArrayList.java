package com.danieli1818.drminigames.common.configurationserializables.collections.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.danieli1818.drminigames.common.exceptions.InvalidConfigurationDataException;

public class ConfigurationSerializableArrayList<E extends ConfigurationSerializable> implements ConfigurationSerializableList<E> {

	private List<E> list;
	
	public ConfigurationSerializableArrayList() {
		this.list = new ArrayList<E>();
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		int index = 0;
		for (E element : this.list) {
			map.put(String.valueOf(index), element);
			index++;
		}
		return map;
	}

	@Override
	public boolean add(E e) {
		return this.list.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return this.list.addAll(c);
	}

	@Override
	public void clear() {
		this.list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.list.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return this.list.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return this.list.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.list.retainAll(c);
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public Object[] toArray() {
		return this.list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.list.toArray(a);
	}

	@Override
	public void add(int index, E element) {
		this.list.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return this.list.addAll(index, c);
	}

	@Override
	public E get(int index) {
		return this.list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return this.list.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return this.list.listIterator(index);
	}

	@Override
	public E remove(int index) {
		return this.list.remove(index);
	}

	@Override
	public E set(int index, E element) {
		return this.list.set(index, element);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return this.list.subList(fromIndex, toIndex);
	}
	
	public static ConfigurationSerializableArrayList<ConfigurationSerializable> deserialize(Map<String, Object> map) {
		
		ConfigurationSerializableArrayList<ConfigurationSerializable> list = new ConfigurationSerializableArrayList<>();
		
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				list.add(Integer.parseInt(entry.getKey()), (ConfigurationSerializable)entry.getValue());
			} catch (NumberFormatException e) {
				
			}
		}
		
		return list;
		
	}

}
