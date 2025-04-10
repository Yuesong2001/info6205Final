package adt.impl;

import adt.MyMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Hash map implementation based on chaining method
 * @param <K> Type of keys
 * @param <V> Type of values
 */
public class HashMap<K, V> implements MyMap<K, V> {
    // Default initial capacity - must be a power of 2
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    // Default load factor
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    // Bucket array, each bucket is a linked list
    private LinkedList<Entry<K, V>>[] table;
    
    // Number of key-value pairs in the hash map
    private int size;
    
    // Load factor
    private final float loadFactor;
    
    // Resize threshold = capacity * load factor
    private int threshold;
    
    /**
     * Constructs a hash map with default capacity and load factor
     */
    public HashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    
    /**
     * Constructs a hash map with specified capacity and default load factor
     * @param initialCapacity Initial capacity
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    
    /**
     * Constructs a hash map with specified capacity and load factor
     * @param initialCapacity Initial capacity
     * @param loadFactor Load factor
     */
    @SuppressWarnings("unchecked")
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        
        // Ensure capacity is a power of 2
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        
        this.loadFactor = loadFactor;
        table = new LinkedList[capacity];
        threshold = (int)(capacity * loadFactor);
    }
    
    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        // Resize if necessary
        if (size >= threshold) {
            resize();
        }
        
        int index = getIndex(key);
        
        // If bucket is empty, create a linked list
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }
        
        // Check if key already exists
        for (Entry<K, V> entry : table[index]) {
            if (entry.getKey().equals(key)) {
                V oldValue = entry.getValue();
                entry.setValue(value);
                return oldValue;
            }
        }
        
        // Key doesn't exist, add new entry
        table[index].add(new SimpleEntry<>(key, value));
        size++;
        return null;
    }
    
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        int index = getIndex(key);
        
        if (table[index] == null) {
            return null;
        }
        
        // Find key in the linked list
        for (Entry<K, V> entry : table[index]) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        int index = getIndex(key);
        
        if (table[index] == null) {
            return null;
        }
        
        // Find and remove key in the linked list
        for (int i = 0; i < table[index].size(); i++) {
            Entry<K, V> entry = table[index].get(i);
            if (entry.getKey().equals(key)) {
                table[index].remove(i);
                size--;
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        int index = getIndex(key);
        
        if (table[index] == null) {
            return false;
        }
        
        // Find key in the linked list
        for (Entry<K, V> entry : table[index]) {
            if (entry.getKey().equals(key)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }
    
    @Override
    public List<K> keySet() {
        List<K> keys = new ArrayList<>();
        
        for (LinkedList<Entry<K, V>> bucket : table) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    keys.add(entry.getKey());
                }
            }
        }
        
        return keys;
    }
    
    @Override
    public List<V> values() {
        List<V> values = new ArrayList<>();
        
        for (LinkedList<Entry<K, V>> bucket : table) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    values.add(entry.getValue());
                }
            }
        }
        
        return values;
    }
    
    /**
     * Get hash index for the key
     * @param key Key
     * @return Index
     */
    private int getIndex(K key) {
        // Use & operation instead of % operation, since table length is a power of 2
        return (key.hashCode() & 0x7FFFFFFF) % table.length;
    }
    
    /**
     * Resize the hash table
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = table.length * 2;
        LinkedList<Entry<K, V>>[] newTable = new LinkedList[newCapacity];
        
        // Recalculate position for each entry
        for (LinkedList<Entry<K, V>> bucket : table) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    int index = (entry.getKey().hashCode() & 0x7FFFFFFF) % newCapacity;
                    
                    if (newTable[index] == null) {
                        newTable[index] = new LinkedList<>();
                    }
                    
                    newTable[index].add(entry);
                }
            }
        }
        
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }
    
    @Override
    public V putIfAbsent(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        // If key already exists, return existing value
        if (containsKey(key)) {
            return get(key);
        }
        
        // If key doesn't exist, add new key-value pair and return null
        put(key, value);
        return null;
    }
    
    /**
     * Simple implementation of hash map entry
     */
    private static class SimpleEntry<K, V> implements Entry<K, V> {
        private final K key;
        private V value;
        
        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            return key;
        }
        
        @Override
        public V getValue() {
            return value;
        }
        
        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
            SimpleEntry<?, ?> that = (SimpleEntry<?, ?>) o;
            
            if (key != null ? !key.equals(that.key) : that.key != null) return false;
            return value != null ? value.equals(that.value) : that.value == null;
        }
        
        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
        
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}