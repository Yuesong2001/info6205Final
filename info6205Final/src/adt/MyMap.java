package adt;

import java.util.List;

/**
 * 哈希映射接口 - 定义键值对映射的基本操作
 * @param <K> 键的类型
 * @param <V> 值的类型
 */
public interface MyMap<K, V> {
    /**
     * 将指定键值对放入映射中
     * @param key 键
     * @param value 值
     * @return 如果键已存在，返回旧值；否则返回null
     */
    V put(K key, V value);
    
    /**
     * 获取指定键关联的值
     * @param key 键
     * @return 关联的值，如果键不存在则返回null
     */
    V get(K key);
    
    /**
     * 删除指定键的映射
     * @param key 键
     * @return 被删除的值，如果键不存在则返回null
     */
    V remove(K key);
    
    /**
     * 检查映射中是否包含指定键
     * @param key 键
     * @return 包含则返回true，否则返回false
     */
    boolean containsKey(K key);
    
    /**
     * 获取映射中键值对的数量
     * @return 键值对数量
     */
    int size();
    
    /**
     * 检查映射是否为空
     * @return 为空则返回true，否则返回false
     */
    boolean isEmpty();
    
    /**
     * 清空映射中的所有键值对
     */
    void clear();
    
    /**
     * 获取所有键的集合
     * @return 包含所有键的列表
     */
    List<K> keySet();
    
    /**
     * 获取所有值的集合
     * @return 包含所有值的列表
     */
    List<V> values();
    
    V putIfAbsent(K key, V value);
    
    /**
     * 代表哈希映射中的键值对
     */
    interface Entry<K, V> {
        /**
         * 获取键
         * @return 键
         */
        K getKey();
        
        /**
         * 获取值
         * @return 值
         */
        V getValue();
        
        /**
         * 设置新值
         * @param value 新值
         * @return 旧值
         */
        V setValue(V value);
        
        
    }
    
    
}