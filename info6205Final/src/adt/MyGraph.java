package adt;
import java.util.Set;

public interface MyGraph<V> {
    /**
     * add vertex
     * @param vertex the vertex to add
     * @return Returns true if the vertex was successfully added, false if the vertex already exists
     */
    boolean addVertex(V vertex);
    
    /**
    * Add an edge
    * @param source source vertex
    * @param destination destination vertex
    * @return Returns true if the edge is successfully added, false if the edge already exists
    */
    boolean addEdge(V source, V destination);
    
    /**
    * Check if the vertex exists
    * @param vertex vertex
    * @return Returns true if the vertex exists, otherwise returns false
    */
    boolean containsVertex(V vertex);
    
    /**
    * Check if the edge exists
    * @param source source vertex
    * @param destination destination vertex
    * @return Returns true if the edge exists, otherwise returns false
    */
    boolean containsEdge(V source, V destination);
    
    /**
    * Delete a vertex and all its associated edges
    * @param vertex The vertex to be deleted
    * @return Returns true if the vertex is successfully deleted, false if the vertex does not exist
    */
    boolean removeVertex(V vertex);
    
    /**
    * Delete edge
    * @param source source vertex
    * @param destination destination vertex
    * @return Returns true if the edge is successfully deleted, false if the edge does not exist
    */
    boolean removeEdge(V source, V destination);
    
    /**
    * Get all vertices
    * @return a collection containing all vertices
    */
    Set<V> getVertices();
    
    /**
    * Get all neighbors of a vertex (directly connected vertices)
    * @param vertex vertex
    * @return a collection of all neighbor vertices
    */
    Set<V> getNeighbors(V vertex);
    
    /**
    * Get the number of vertices in the graph
    * @return the number of vertices
    */
    int getVertexCount();
    
    /**
    * Get the number of edges in the graph
    * @return the number of edges
    */
    int getEdgeCount();
    
    /**
    * Check if the graph is empty (no vertices)
    * @return Returns true if the graph is empty, false otherwise
    */
    boolean isEmpty();
    
    /**
    * Clear all vertices and edges in the graph
    */
    void clear();
    
    boolean putIfAbsent(V vertex);
}