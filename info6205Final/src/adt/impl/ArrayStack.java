package adt.impl;

import adt.MyStack;

/**
 * Stack implementation based on array
 * @param <T> Type of elements in the stack
 */
public class ArrayStack<T> implements MyStack<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int top; // Stack top pointer
    
    /**
     * Creates an empty stack with default capacity
     */
    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates an empty stack with specified capacity
     * @param initialCapacity Initial capacity
     */
    public ArrayStack(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        elements = new Object[initialCapacity];
        top = -1; // Top pointer of an empty stack is -1
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
        elements[top--] = null; // Avoid memory leak
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
        // Clear references to avoid memory leak
        for (int i = 0; i <= top; i++) {
            elements[i] = null;
        }
        top = -1;
    }
    
    /**
     * Ensure the stack has enough capacity
     */
    private void ensureCapacity() {
        if (top == elements.length - 1) {
            // Double the capacity
            int newCapacity = elements.length * 2;
            Object[] newElements = new Object[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }
    
    /**
     * Custom empty stack exception
     */
    public static class EmptyStackException extends RuntimeException {
        public EmptyStackException(String message) {
            super(message);
        }
    }
}
