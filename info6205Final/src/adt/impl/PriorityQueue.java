package adt.impl;

import adt.MyPriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Priority queue implementation based on binary heap
 * @param <T> Type of elements in the queue
 */
public class PriorityQueue<T> implements MyPriorityQueue<T> {
    // Default initial capacity
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    
    // Array for storing the heap
    private Object[] heap;
    
    // Number of elements in the queue
    private int size;
    
    // Comparator used to determine priority
    private final Comparator<? super T> comparator;
    
    /**
     * Creates an empty priority queue using elements' natural ordering
     */
    public PriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }
    
    /**
     * Creates an empty priority queue using the given comparator
     * @param comparator Comparator used to determine priority
     */
    public PriorityQueue(Comparator<? super T> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }
    
    /**
     * Creates an empty priority queue with specified initial capacity using the given comparator
     * @param initialCapacity Initial capacity
     * @param comparator Comparator used to determine priority
     */
    public PriorityQueue(int initialCapacity, Comparator<? super T> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        
        this.heap = new Object[initialCapacity];
        this.comparator = comparator;
    }
    
    @Override
    public void add(T item) {
        if (item == null) {
            throw new NullPointerException("Cannot add null element to priority queue");
        }
        
        // If capacity expansion is needed
        if (size >= heap.length) {
            grow();
        }
        
        siftUp(size, item);
        size++;
    }
    
    @Override
    public T remove() {
        if (isEmpty()) {
            throw new EmptyPriorityQueueException("Priority queue is empty");
        }
        
        int lastIdx = size - 1;
        @SuppressWarnings("unchecked")
        T result = (T) heap[0]; // Root element (highest priority)
        T lastElement = (T) heap[lastIdx];
        heap[lastIdx] = null; // Clear reference to the last element
        size--;
        
        if (size > 0) {
            // Move the last element to the root position, then adjust downward
            siftDown(0, lastElement);
        }
        
        return result;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new EmptyPriorityQueueException("Priority queue is empty");
        }
        
        return (T) heap[0];
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public void clear() {
        // Clear all element references to help GC
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }
    
    public List<T> toList() {
        List<T> result = new ArrayList<>(size());
        
        // Create a copy of the heap so we can remove elements without modifying the original heap
        Object[] copy = Arrays.copyOf(heap, size);
        int copySize = size;
        
        // Simulate removing the heap's root element repeatedly, adding it to the result list
        while (copySize > 0) {
            @SuppressWarnings("unchecked")
            T root = (T) copy[0]; // Get the root element
            result.add(root);
            
            // Move the last element to the root position
            copy[0] = copy[--copySize];
            if (copySize > 0) {
                // Adjust the heap downward
            	siftDownCopy(0, (T)copy[0], copy, copySize);
            }
        }
        
        return result;
    }

    /**
     * Perform downward adjustment on heap copy, used for toList method
     */
    @SuppressWarnings("unchecked")
    private void siftDownCopy(int k, T item, Object[] copy, int copySize) {
        if (comparator != null) {
            siftDownUsingComparatorCopy(k, item, copy, copySize);
        } else {
            siftDownComparableCopy(k, item, copy, copySize);
        }
    }

    /**
     * Perform downward adjustment using elements' natural ordering on heap copy
     */
    @SuppressWarnings("unchecked")
    private void siftDownComparableCopy(int k, T item, Object[] copy, int copySize) {
        Comparable<? super T> key = (Comparable<? super T>) item;
        int half = copySize >>> 1;
        
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = copy[child];
            int right = child + 1;
            
            if (right < copySize && ((Comparable<? super T>) c).compareTo((T) copy[right]) > 0) {
                c = copy[child = right];
            }
            
            if (key.compareTo((T) c) <= 0) {
                break;
            }
            
            copy[k] = c;
            k = child;
        }
        
        copy[k] = key;
    }

    /**
     * Perform downward adjustment using comparator on heap copy
     */
    @SuppressWarnings("unchecked")
    private void siftDownUsingComparatorCopy(int k, T item, Object[] copy, int copySize) {
        int half = copySize >>> 1;
        
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = copy[child];
            int right = child + 1;
            
            if (right < copySize && comparator.compare((T) c, (T) copy[right]) > 0) {
                c = copy[child = right];
            }
            
            if (comparator.compare(item, (T) c) <= 0) {
                break;
            }
            
            copy[k] = c;
            k = child;
        }
        
        copy[k] = item;
    }
    
    /**
     * Remove all elements that satisfy the given predicate
     * @param filter Predicate used to determine whether to remove an element
     * @return true if the queue was changed as a result of the removal operation
     */
    public boolean removeIf(Predicate<? super T> filter) {
        if (filter == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        boolean removed = false;
        
        // Create a new array to store elements that should not be removed
        Object[] newHeap = new Object[heap.length];
        int newSize = 0;
        
        // Traverse the current heap, keeping elements that don't satisfy the removal condition
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T element = (T) heap[i];
            
            // Keep the element if it doesn't satisfy the filter condition
            if (!filter.test(element)) {
                newHeap[newSize++] = element;
            } else {
                removed = true; // Mark that an element was removed
            }
        }
        
        // If elements were removed, rebuild the heap
        if (removed) {
            // Update the heap and size
            heap = newHeap;
            size = newSize;
            
            // Rebuild the heap (starting from the last non-leaf node and moving upward)
            for (int i = (size >>> 1) - 1; i >= 0; i--) {
                @SuppressWarnings("unchecked")
                T element = (T) heap[i];
                siftDown(i, element);
            }
        }
        
        return removed;
    }
    
    /**
     * Expand array capacity
     */
    private void grow() {
        int oldCapacity = heap.length;
        int newCapacity = oldCapacity + (oldCapacity < 64 ? oldCapacity + 2 : oldCapacity >> 1);
        heap = Arrays.copyOf(heap, newCapacity);
    }
    
    /**
     * Adjust the position of a newly added element upward
     * @param k Position of the new element
     * @param item New element
     */
    @SuppressWarnings("unchecked")
    private void siftUp(int k, T item) {
        if (comparator != null) {
            siftUpUsingComparator(k, item);
        } else {
            siftUpComparable(k, item);
        }
    }
    
    /**
     * Adjust upward using elements' natural ordering
     * @param k Position of the new element
     * @param item New element
     */
    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, T item) {
        Comparable<? super T> key = (Comparable<? super T>) item;
        
        while (k > 0) {
            int parent = (k - 1) >>> 1; // Parent node index
            Object e = heap[parent];
            if (key.compareTo((T) e) >= 0) {
                break; // If the new element is greater than or equal to the parent, stop adjusting
            }
            heap[k] = e; // Parent node moves down
            k = parent;
        }
        
        heap[k] = key;
    }
    
    /**
     * Adjust upward using comparator
     * @param k Position of the new element
     * @param item New element
     */
    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, T item) {
        while (k > 0) {
            int parent = (k - 1) >>> 1; // Parent node index
            Object e = heap[parent];
            if (comparator.compare(item, (T) e) >= 0) {
                break; // If the new element is greater than or equal to the parent, stop adjusting
            }
            heap[k] = e; // Parent node moves down
            k = parent;
        }
        
        heap[k] = item;
    }
    
    /**
     * Adjust the position of an element downward
     * @param k Starting position of the element
     * @param item Element to adjust
     */
    @SuppressWarnings("unchecked")
    private void siftDown(int k, T item) {
        if (comparator != null) {
            siftDownUsingComparator(k, item);
        } else {
            siftDownComparable(k, item);
        }
    }
    
    /**
     * Adjust downward using elements' natural ordering
     * @param k Starting position of the element
     * @param item Element to adjust
     */
    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, T item) {
        Comparable<? super T> key = (Comparable<? super T>) item;
        int half = size >>> 1; // Index of the last non-leaf node
        
        while (k < half) {
            int child = (k << 1) + 1; // Left child node index
            Object c = heap[child];
            int right = child + 1;
            
            // If right child exists and has higher priority than left child
            if (right < size && ((Comparable<? super T>) c).compareTo((T) heap[right]) > 0) {
                c = heap[child = right];
            }
            
            // If key's priority is not lower than the child's, stop adjusting
            if (key.compareTo((T) c) <= 0) {
                break;
            }
            
            heap[k] = c; // Child node moves up
            k = child;
        }
        
        heap[k] = key;
    }
    
    /**
     * Adjust downward using comparator
     * @param k Starting position of the element
     * @param item Element to adjust
     */
    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, T item) {
        int half = size >>> 1; // Index of the last non-leaf node
        
        while (k < half) {
            int child = (k << 1) + 1; // Left child node index
            Object c = heap[child];
            int right = child + 1;
            
            // If right child exists and has higher priority than left child
            if (right < size && comparator.compare((T) c, (T) heap[right]) > 0) {
                c = heap[child = right];
            }
            
            // If item's priority is not lower than the child's, stop adjusting
            if (comparator.compare(item, (T) c) <= 0) {
                break;
            }
            
            heap[k] = c; // Child node moves up
            k = child;
        }
        
        heap[k] = item;
    }
    
    /**
     * Custom empty priority queue exception
     */
    public static class EmptyPriorityQueueException extends RuntimeException {
        public EmptyPriorityQueueException(String message) {
            super(message);
        }
    }
}