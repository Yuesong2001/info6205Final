package adt.impl;

import adt.MyStack;

/**
 * 基于数组的栈实现
 * @param <T> 栈中元素的类型
 */
public class ArrayStack<T> implements MyStack<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int top; // 栈顶指针
    
    /**
     * 创建一个默认容量的空栈
     */
    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * 创建一个指定容量的空栈
     * @param initialCapacity 初始容量
     */
    public ArrayStack(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        elements = new Object[initialCapacity];
        top = -1; // 空栈的栈顶指针为-1
    }
    
    @Override
    public void push(T item) {
        ensureCapacity();
        elements[++top] = item;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException("Stack is empty");
        }
        T result = (T) elements[top];
        elements[top--] = null; // 避免内存泄漏
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException("Stack is empty");
        }
        return (T) elements[top];
    }
    
    @Override
    public boolean isEmpty() {
        return top == -1;
    }
    
    @Override
    public int size() {
        return top + 1;
    }
    
    @Override
    public void clear() {
        // 清空引用避免内存泄漏
        for (int i = 0; i <= top; i++) {
            elements[i] = null;
        }
        top = -1;
    }
    
    /**
     * 确保栈有足够的容量
     */
    private void ensureCapacity() {
        if (top == elements.length - 1) {
            // 扩容为原来的两倍
            int newCapacity = elements.length * 2;
            Object[] newElements = new Object[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }
    
    /**
     * 自定义栈为空异常
     */
    public static class EmptyStackException extends RuntimeException {
        public EmptyStackException(String message) {
            super(message);
        }
    }
}
