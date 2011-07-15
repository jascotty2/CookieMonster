/**
 * Programmer: Jacob Scott
 * Program Name: HashMap3D
 * Description: wrapper for dynamic 3-d plots
 * Date: Jun 9, 2011
 */
package com.jascotty2.util;

import java.util.HashMap;

/**
 * @param <V> the type of mapped values
 * @author jacob
 */
public class HashMap3D<V> implements Cloneable {

    private HashMap<Integer, HashMap<Integer, HashMap<Integer, V>>> map =
            new HashMap<Integer, HashMap<Integer, HashMap<Integer, V>>>();//x, y, z;

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * 
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #contains contains} operation may be used to
     * distinguish these two cases.
     * 
     * @param x
     * @param y
     * @param z
     * @return
     */
    public V get(int x, int y, int z) {
        if (map.containsKey(x)
                && map.get(x).containsKey(y)
                && map.get(x).get(y).containsKey(z)) {
            return map.get(x).get(y).get(z);
        }
        return null;
    }
    
    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified key.
     * 
     * @param x
     * @param y
     * @param z
     * @return <tt>true</tt> if this map contains a mapping for the specified key.
     */
    public boolean contains(int x, int y, int z) {
        return map.containsKey(x)
                && map.get(x).containsKey(y)
                && map.get(x).get(y).containsKey(z);
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     * @param x
     * @param y
     * @param z
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *          <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *          (A <tt>null</tt> return can also indicate that the map
     *          previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(int x, int y, int z, V value) {
        if (!map.containsKey(x)) {
            map.put(x, new HashMap<Integer, HashMap<Integer, V>>());
        }
        if (!map.get(x).containsKey(y)) {
            map.get(x).put(y, new HashMap<Integer, V>());
        }
        return map.get(x).get(y).put(z, value);
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * @param x
     * @param y
     * @param z
     * @return the previous value associated with <tt>key</tt>, or
     *          <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *          (A <tt>null</tt> return can also indicate that the map
     *          previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V remove(int x, int y, int z) {
        if (map.containsKey(x)
                && map.get(x).containsKey(y)
                && map.get(x).get(y).containsKey(z)) {
            return map.get(x).get(y).remove(z);
        }
        return null;
    }
} // end class HashMap3D

