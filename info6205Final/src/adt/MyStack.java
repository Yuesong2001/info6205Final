package adt;

/**
 * 栈接口 - 定义栈的基本操作
 * @param <T> 栈中元素的类型
 */
public interface MyStack<T> {
    /**
     * 将元素推入栈顶
     * @param item 要推入的元素
     */
    void push(T item);
    
    /**
     * 从栈顶弹出并返回元素
     * @return 栈顶元素
     * @throws EmptyStackException 如果栈为空
     */
    T pop();
    
    /**
     * 查看栈顶元素但不移除
     * @return 栈顶元素
     * @throws EmptyStackException 如果栈为空
     */
    T peek();
    
    /**
     * 检查栈是否为空
     * @return 栈为空时返回true，否则返回false
     */
    boolean isEmpty();
    
    /**
     * 返回栈中元素个数
     * @return 栈中元素个数
     */
    int size();
    
    /**
     * 清空栈中所有元素
     */
    void clear();
}