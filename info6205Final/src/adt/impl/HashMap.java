package adt.impl;

import adt.MyMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于链表法的哈希映射实现
 * @param <K> 键的类型
 * @param <V> 值的类型
 */
public class HashMap<K, V> implements MyMap<K, V> {
    // 默认初始容量 - 必须是2的幂
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    // 默认负载因子
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    // 桶数组，每个桶是一个链表
    private LinkedList<Entry<K, V>>[] table;
    
    // 哈希映射中键值对的数量
    private int size;
    
    // 负载因子
    private final float loadFactor;
    
    // 扩容阈值 = 容量 * 负载因子
    private int threshold;
    
    /**
     * 使用默认容量和负载因子构造哈希映射
     */
    public HashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    
    /**
     * 使用指定容量和默认负载因子构造哈希映射
     * @param initialCapacity 初始容量
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    
    /**
     * 使用指定容量和负载因子构造哈希映射
     * @param initialCapacity 初始容量
     * @param loadFactor 负载因子
     */
    @SuppressWarnings("unchecked")
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        
        // 确保容量是2的幂
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
        
        // 如果需要扩容
        if (size >= threshold) {
            resize();
        }
        
        int index = getIndex(key);
        
        // 如果桶为空，创建链表
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }
        
        // 检查是否键已存在
        for (Entry<K, V> entry : table[index]) {
            if (entry.getKey().equals(key)) {
                V oldValue = entry.getValue();
                entry.setValue(value);
                return oldValue;
            }
        }
        
        // 键不存在，添加新条目
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
        
        // 在链表中查找键
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
        
        // 在链表中查找并删除键
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
        
        // 在链表中查找键
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
     * 获取键的哈希索引
     * @param key 键
     * @return 索引
     */
    private int getIndex(K key) {
        // 使用 & 操作代替 % 操作，因为表长度是2的幂
        return (key.hashCode() & 0x7FFFFFFF) % table.length;
    }
    
    /**
     * 扩容哈希表
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = table.length * 2;
        LinkedList<Entry<K, V>>[] newTable = new LinkedList[newCapacity];
        
        // 重新计算每个条目的位置
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
        
        // 如果键已存在，返回现有值
        if (containsKey(key)) {
            return get(key);
        }
        
        // 如果键不存在，添加新键值对并返回null
        put(key, value);
        return null;
    }
    
    /**
     * 哈希映射条目的简单实现
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