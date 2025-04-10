package adt;

/**
* Stack interface - defines basic operations of the stack
* @param <T> the type of elements in the stack
*/
public interface MyStack<T> {
	/**
	* Push an element to the top of the stack
	* @param item The element to be pushed
	*/
    void push(T item);
    
    /**
    * Pop and return the element from the top of the stack
    * @return the top element of the stack
    * @throws EmptyStackException if the stack is empty
    */
    T pop();
    
    /**
    * View the top element of the stack but do not remove it
    * @return the top element of the stack
    * @throws EmptyStackException if the stack is empty
    */
    T peek();
    
    /**
    * Check if the stack is empty
    * @return Returns true if the stack is empty, otherwise returns false
    */
    boolean isEmpty();
    
    /**
    * Return the number of elements in the stack
    * @return the number of elements in the stack
    */
    int size();
    
    /**
    * Clear all elements in the stack
    */
    void clear();
}