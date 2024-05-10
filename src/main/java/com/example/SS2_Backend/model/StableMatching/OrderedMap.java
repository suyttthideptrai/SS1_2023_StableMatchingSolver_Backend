package com.example.SS2_Backend.model.StableMatching;

import java.util.*;
import java.lang.Integer;

public class OrderedMap {
    private final Map<Integer, Double> map = new LinkedHashMap<>();
    private final List<Integer> keys = new ArrayList<>();

    // Add or update an entry
    public void put(Integer key, Double value) {
        if (!map.containsKey(key)) {
            keys.add(key);
        }
        map.put(key, value);
    }

    // Get value by key
    public Double get(Integer key) {
        return map.get(key);
    }

    // Get value by index
    public Double getByIndex(int index) {
        Integer key = keys.get(index);
        return map.get(key);
    }

    // Remove an entry
    public void remove(Integer key) {
        if (map.containsKey(key)) {
            map.remove(key);
            keys.remove(key);
        }
    }

    // Reorder the keys
    public void reorder(int fromIndex, int toIndex) {
        keys.add(toIndex, keys.remove(fromIndex));
    }

    public void sortByValueDescending() {
        keys.sort((k1, k2) -> map.get(k2).compareTo(map.get(k1)));
    }

    // Size of the map
    public int size() {
        return map.size();
    }

    // Iterator to traverse keys in their order
    public Iterator<Integer> keyIterator() {
        return keys.iterator();
    }

    @Override
    public String toString() {
        return "OrderedMap{" +
                "map=" + map +
                ", keys=" + keys +
                '}';
    }

    public static void main(String[] args) {
        OrderedMap map = new OrderedMap();
        map.put(1, 5.0);
        map.put(6, 8.0);
        map.put(5, 2.0);
        map.put(4, 9.0);
        map.put(3, 1.0);
        map.put(2, 4.0);
        System.out.println(map);
        map.sortByValueDescending();
        System.out.println(map);
    }
}

