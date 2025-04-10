package adt;

import java.util.Comparator;
import java.util.List;

/**
 * 优先队列接口 - 定义优先队列的基本操作
 * @param <T> 队列中元素的类型
 */
public interface MyPriorityQueue<T> {
    /**
     * 向队列中添加元素
     * @param item 要添加的元素
     */
    void add(T item);
    
    /**
     * 移除并返回队列中具有最高优先级的元素
     * @return 具有最高优先级的元素
     * @throws EmptyPriorityQueueException 如果队列为空
     */
    T remove();
    
    /**
     * 查看具有最高优先级的元素但不移除
     * @return 具有最高优先级的元素
     * @throws EmptyPriorityQueueException 如果队列为空
     */
    T peek();
    
    /**
     * 检查队列是否为空
     * @return 队列为空时返回true，否则返回false
     */
    boolean isEmpty();
    
    /**
     * 返回队列中元素个数
     * @return 队列中元素个数
     */
    int size();
    
    /**
     * 清空队列中的所有元素
     */
    void clear();
    
    /**
     * 将优先队列中的元素转换为列表，不改变原队列
     * @return 包含队列所有元素的列表
     */

}