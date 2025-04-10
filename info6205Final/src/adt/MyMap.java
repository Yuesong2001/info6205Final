package adt;

import java.util.List;

/**
* Hash map interface - defines basic operations for key-value mapping
* @param <K> key type
* @param <V> value type
*/
public interface MyMap<K, V> {
	/**
	* Put the specified key-value pair into the map
	* @param key key
	* @param value value
	* @return If the key already exists, return the old value; otherwise return null
	*/
    V put(K key, V value);
    
    /**
    * Get the value associated with the specified key
    * @param key key
    * @return the associated value, or null if the key does not exist
    */
    V get(K key);
    
    /**
    * Delete the mapping of the specified key
    * @param key key
    * @return the deleted value, or null if the key does not exist
    */
    V remove(K key);
    
    /**
    * Check if the map contains the specified key
    * @param key key
    * @return Returns true if it contains, otherwise returns false
    */
    boolean containsKey(K key);
    
    /**
    * Get the number of key-value pairs in the map
    * @return the number of key-value pairs
    */
    int size();
    
    /**
    * Check if the mapping is empty
    * @return Returns true if it is empty, otherwise returns false
    */
    boolean isEmpty();
    
    /**
    * Clear all key-value pairs in the map
    */
    void clear();
    
    /**
    * Get a collection of all keys
    * @return a list containing all keys
    */
    List<K> keySet();
    
    /**
    * Get a collection of all values
    * @return a list containing all values
    */
    List<V> values();
    
    V putIfAbsent(K key, V value);
    
    /**
    * Represents a key-value pair in a hash map
    */
    interface Entry<K, V> {
    	/**
    	* Get key
    	* @return key
    	*/
        K getKey();
        
        /**
        * Get value
        * @return value
        */
        V getValue();
        
        /**
        * Set new value
        * @param value new value
        * @return old value
        */
        V setValue(V value);
        
        
    }
    
    
}