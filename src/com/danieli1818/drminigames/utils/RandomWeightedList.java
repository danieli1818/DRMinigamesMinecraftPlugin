package com.danieli1818.drminigames.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomWeightedList<T> implements Collection<T> {
	
	private final NavigableMap<Double, List<T>> map = new TreeMap<Double, List<T>>();
	private final Random random = new Random();
	private double total;
	private double defaultWeight;
	
	public RandomWeightedList(double defaultWeight) {
		if (defaultWeight <= 0) {
			defaultWeight = 1;
		}
		this.total = 0;
		this.defaultWeight = defaultWeight;
	}

	@Override
	public boolean add(T e) {
		return add(this.defaultWeight, e);
	}
	
	public boolean add(Double weight, T e) {
		if (!this.map.containsKey(weight)) {
			this.map.put(weight, new ArrayList<T>());
		}
		this.map.get(weight).add(e);
		this.total += weight;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T t : c) {
			this.add(t);
		}
		return true;
	}

	@Override
	public void clear() {
		this.map.clear();
		this.total = 0;
		
	}

	@Override
	public boolean contains(Object o) {
		for (List<T> list : this.map.values()) {
			for (T t : list) {
				if (t.equals(o)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		int size = 0;
		for (List<T> list : this.map.values()) {
			size += list.size();
		}
		return size;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public T getRandom() {
		double num = this.random.nextDouble() * this.total;
		List<T> list = this.map.higherEntry(num).getValue();
		Collections.shuffle(list);
		return list.get(0);
	}

}
