package com.example.SS2_Backend.model.StableMatching;

import java.util.*;
import java.lang.Integer;

public class OrderedMap {
    private final Map<Integer, Double> map = new LinkedHashMap<>();
    private final List<Integer> keys = new ArrayList<>();

    /**
     *
     * @param key unique identifier of competitor instance
     * @param value score of the respective competitor
     *
     * this method registers new competitor instance to the preference list (OrderedMap)
     */
    public void put(Integer key, Double value) {
        if (!map.containsKey(key)) {
            keys.add(key);
        }
        map.put(key, value);
    }

    /**
     *
     * @param key unique identifier of competitor instance
     * @return score of the respective competitor
     */
    public Double get(int key) {
        return map.get(key);
    }

    /**
     * <i>THIS METHOD ONLY VALUABLE AFTER @method sortByValueDescending IS INVOKED </i>
     * @param key unique identifier of competitor instance
     * @return the position (rank) of the respective competitor in the preference list
     *
     */
    public int getPositionOf(int key) {
        return keys.indexOf(key);
    }

    /**
     * <i>THIS METHOD ONLY VALUABLE AFTER @method sortByValueDescending IS INVOKED </i>
     * @param position position (rank best <-- 0, 1, 2, 3, ... --> worst) on the preference list
     * @return unique identifier of the competitor instance that holds the respective position on the list
     */
    public int getPositionHolder(int position) {
        return keys.get(position);
    }

    /**
     * <i>THIS METHOD ONLY VALUABLE AFTER @method sortByValueDescending IS INVOKED </i>
     * @param index position (rank best <-- 0, 1, 2, 3, ... --> worst) on the preference list
     * @return score of the respective position on the preference list
     */
    public Double getPositionScore(int index) {
        Integer key = keys.get(index);
        return map.get(key);
    }

    /**
     *
     * @param key unique identifier of competitor instance
     *            this method removes the competitor instance from the preference list
     */
    public void remove(Integer key) {
        if (map.containsKey(key)) {
            map.remove(key);
            keys.remove(key);
        }
    }

//    public void reorder(int fromIndex, int toIndex) {
//        keys.add(toIndex, keys.remove(fromIndex));
//    }

    /**
     * <i>THIS METHOD SHOULD ONLY BE CALLED WHEN ALL INSTANCE OF COMPETITORS ARE PUT TO THE MAP</i>
     * Sorts the @var keys list in descending order by the score of each competitor
     */
    public void sortByValueDescending() {
        keys.sort((k1, k2) -> map.get(k2).compareTo(map.get(k1)));
    }

    // Size of the map
    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
            return map.isEmpty();
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
        map.put(0, 9.1);
        System.out.println(map);
        map.sortByValueDescending();
        System.out.println(map);
    }
}

