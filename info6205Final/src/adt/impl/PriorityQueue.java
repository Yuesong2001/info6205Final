package adt.impl;

import adt.MyPriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * 基于二叉堆的优先队列实现
 * @param <T> 队列中元素的类型
 */
public class PriorityQueue<T> implements MyPriorityQueue<T> {
    // 默认初始容量
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    
    // 存储堆的数组
    private Object[] heap;
    
    // 队列中元素的个数
    private int size;
    
    // 比较器，用于确定优先级
    private final Comparator<? super T> comparator;
    
    /**
     * 创建一个空的优先队列，使用元素的自然顺序
     */
    public PriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }
    
    /**
     * 创建一个空的优先队列，使用给定的比较器
     * @param comparator 用于确定优先级的比较器
     */
    public PriorityQueue(Comparator<? super T> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }
    
    /**
     * 创建一个指定初始容量的空优先队列，使用给定的比较器
     * @param initialCapacity 初始容量
     * @param comparator 用于确定优先级的比较器
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
        
        // 如果需要扩容
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
        T result = (T) heap[0]; // 根元素（最高优先级）
        T lastElement = (T) heap[lastIdx];
        heap[lastIdx] = null; // 清除最后一个元素的引用
        size--;
        
        if (size > 0) {
            // 将最后一个元素放到根位置，然后向下调整
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
        // 清除所有元素的引用以帮助GC
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }
    
    public List<T> toList() {
        List<T> result = new ArrayList<>(size());
        
        // 创建堆的副本，这样我们可以在不修改原始堆的情况下移除元素
        Object[] copy = Arrays.copyOf(heap, size);
        int copySize = size;
        
        // 模拟不断移除堆顶元素，将其添加到结果列表中
        while (copySize > 0) {
            @SuppressWarnings("unchecked")
            T root = (T) copy[0]; // 取堆顶元素
            result.add(root);
            
            // 将最后一个元素移到根位置
            copy[0] = copy[--copySize];
            if (copySize > 0) {
                // 向下调整堆
            	siftDownCopy(0, (T)copy[0], copy, copySize);
            }
        }
        
        return result;
    }

    /**
     * 在堆副本上执行向下调整，用于toList方法
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
     * 在堆副本上使用元素的自然顺序进行向下调整
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
     * 在堆副本上使用比较器进行向下调整
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
     * 移除满足给定谓词的所有元素
     * @param filter 用于确定是否移除元素的谓词
     * @return 如果队列因移除操作而改变，则返回true
     */
    public boolean removeIf(Predicate<? super T> filter) {
        if (filter == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        boolean removed = false;
        
        // 创建一个新的数组来存储不被移除的元素
        Object[] newHeap = new Object[heap.length];
        int newSize = 0;
        
        // 遍历当前堆，保留不满足移除条件的元素
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T element = (T) heap[i];
            
            // 如果元素不满足过滤条件，则保留
            if (!filter.test(element)) {
                newHeap[newSize++] = element;
            } else {
                removed = true; // 标记有元素被移除
            }
        }
        
        // 如果有元素被移除，需要重建堆
        if (removed) {
            // 更新堆和大小
            heap = newHeap;
            size = newSize;
            
            // 重建堆（从最后一个非叶子节点开始向上调整）
            for (int i = (size >>> 1) - 1; i >= 0; i--) {
                @SuppressWarnings("unchecked")
                T element = (T) heap[i];
                siftDown(i, element);
            }
        }
        
        return removed;
    }
    
    /**
     * 扩展数组容量
     */
    private void grow() {
        int oldCapacity = heap.length;
        int newCapacity = oldCapacity + (oldCapacity < 64 ? oldCapacity + 2 : oldCapacity >> 1);
        heap = Arrays.copyOf(heap, newCapacity);
    }
    
    /**
     * 向上调整新添加的元素的位置
     * @param k 新元素的位置
     * @param item 新元素
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
     * 使用元素的自然顺序进行向上调整
     * @param k 新元素的位置
     * @param item 新元素
     */
    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, T item) {
        Comparable<? super T> key = (Comparable<? super T>) item;
        
        while (k > 0) {
            int parent = (k - 1) >>> 1; // 父节点索引
            Object e = heap[parent];
            if (key.compareTo((T) e) >= 0) {
                break; // 如果新元素大于等于父节点，则停止调整
            }
            heap[k] = e; // 父节点下移
            k = parent;
        }
        
        heap[k] = key;
    }
    
    /**
     * 使用比较器进行向上调整
     * @param k 新元素的位置
     * @param item 新元素
     */
    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, T item) {
        while (k > 0) {
            int parent = (k - 1) >>> 1; // 父节点索引
            Object e = heap[parent];
            if (comparator.compare(item, (T) e) >= 0) {
                break; // 如果新元素大于等于父节点，则停止调整
            }
            heap[k] = e; // 父节点下移
            k = parent;
        }
        
        heap[k] = item;
    }
    
    /**
     * 向下调整元素的位置
     * @param k 元素的起始位置
     * @param item 要调整的元素
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
     * 使用元素的自然顺序进行向下调整
     * @param k 元素的起始位置
     * @param item 要调整的元素
     */
    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, T item) {
        Comparable<? super T> key = (Comparable<? super T>) item;
        int half = size >>> 1; // 最后一个非叶子节点的索引
        
        while (k < half) {
            int child = (k << 1) + 1; // 左子节点索引
            Object c = heap[child];
            int right = child + 1;
            
            // 如果右子节点存在且优先级高于左子节点
            if (right < size && ((Comparable<? super T>) c).compareTo((T) heap[right]) > 0) {
                c = heap[child = right];
            }
            
            // 如果key优先级不低于子节点，则停止调整
            if (key.compareTo((T) c) <= 0) {
                break;
            }
            
            heap[k] = c; // 子节点上移
            k = child;
        }
        
        heap[k] = key;
    }
    
    /**
     * 使用比较器进行向下调整
     * @param k 元素的起始位置
     * @param item 要调整的元素
     */
    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, T item) {
        int half = size >>> 1; // 最后一个非叶子节点的索引
        
        while (k < half) {
            int child = (k << 1) + 1; // 左子节点索引
            Object c = heap[child];
            int right = child + 1;
            
            // 如果右子节点存在且优先级高于左子节点
            if (right < size && comparator.compare((T) c, (T) heap[right]) > 0) {
                c = heap[child = right];
            }
            
            // 如果item优先级不低于子节点，则停止调整
            if (comparator.compare(item, (T) c) <= 0) {
                break;
            }
            
            heap[k] = c; // 子节点上移
            k = child;
        }
        
        heap[k] = item;
    }
    
    /**
     * 自定义优先队列为空异常
     */
    public static class EmptyPriorityQueueException extends RuntimeException {
        public EmptyPriorityQueueException(String message) {
            super(message);
        }
    }
}