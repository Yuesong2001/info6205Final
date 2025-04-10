package adt;

import java.util.Comparator;
import java.util.List;

/**
* Priority queue interface - defines the basic operations of the priority queue
* @param <T> the type of elements in the queue
*/
public interface MyPriorityQueue<T> {
	/**
	* Add an element to the queue
	* @param item The element to be added
	*/
    void add(T item);
    
    /**
    * Remove and return the element with the highest priority in the queue
    * @return the element with the highest priority
    * @throws EmptyPriorityQueueException if the queue is empty
    */
    T remove();
    
    /**
    * Check the element with the highest priority but do not remove it
    * @return the element with the highest priority
    * @throws EmptyPriorityQueueException if the queue is empty
    */
    T peek();
    
    /**
    * Check if the queue is empty
    * @return Returns true if the queue is empty, otherwise returns false
    */
    boolean isEmpty();
    
    /**
    * Return the number of elements in the queue
    * @return the number of elements in the queue
    */
    int size();
    
    /**
    * Clear all elements in the queue
    */
    void clear();
    
    /**
    * Convert the elements in the priority queue to a list without changing the original queue
    * @return a list containing all the elements in the queue
    */

}