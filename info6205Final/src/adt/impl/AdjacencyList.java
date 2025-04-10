// AdjacencyList implementation
package adt.impl;

import adt.MyGraph;
import java.util.*;

/**
 * Graph implementation based on adjacency list
 * Using simple arrays and lists to avoid built-in HashMap and HashSet
 * @param <V> Type of vertices
 */
public class AdjacencyList<V> implements MyGraph<V> {
    // For storing vertices
    private final List<V> vertices;
    // For storing neighbors of each vertex
    private final List<List<V>> adjacencyLists;
    private int edgeCount;
    
    /**
     * Creates an empty graph
     */
    public AdjacencyList() {
        this.vertices = new ArrayList<>();
        this.adjacencyLists = new ArrayList<>();
        this.edgeCount = 0;
    }
    
    /**
     * Find the index of a vertex
     * @param vertex The vertex to find
     * @return The index of the vertex, or -1 if it doesn't exist
     */
    private int indexOf(V vertex) {
        for (int i = 0; i < vertices.size(); i++) {
            if (Objects.equals(vertices.get(i), vertex)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public boolean addVertex(V vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex cannot be null");
        }
        
        if (containsVertex(vertex)) {
            return false;
        }
        
        vertices.add(vertex);
        adjacencyLists.add(new ArrayList<>());
        return true;
    }
    
    @Override
    public boolean addEdge(V source, V destination) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Vertices cannot be null");
        }
        
        // Add vertices if they don't exist
        if (!containsVertex(source)) {
            addVertex(source);
        }
        
        if (!containsVertex(destination)) {
            addVertex(destination);
        }
        
        // Check if the edge already exists
        if (containsEdge(source, destination)) {
            return false;
        }
        
        // Add the edge
        int sourceIndex = indexOf(source);
        List<V> neighbors = adjacencyLists.get(sourceIndex);
        neighbors.add(destination);
        edgeCount++;
        return true;
    }
    
    @Override
    public boolean containsVertex(V vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex cannot be null");
        }
        
        return indexOf(vertex) >= 0;
    }
    
    @Override
    public boolean containsEdge(V source, V destination) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Vertices cannot be null");
        }
        
        int sourceIndex = indexOf(source);
        if (sourceIndex < 0) {
            return false;
        }
        
        List<V> neighbors = adjacencyLists.get(sourceIndex);
        for (V neighbor : neighbors) {
            if (Objects.equals(neighbor, destination)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean removeVertex(V vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex cannot be null");
        }
        
        int vertexIndex = indexOf(vertex);
        if (vertexIndex < 0) {
            return false;
        }
        
        // Remove all edges where this vertex is the destination
        for (int i = 0; i < adjacencyLists.size(); i++) {
            List<V> neighbors = adjacencyLists.get(i);
            for (int j = 0; j < neighbors.size(); j++) {
                if (Objects.equals(neighbors.get(j), vertex)) {
                    neighbors.remove(j);
                    edgeCount--;
                    j--; // Adjust index
                }
            }
        }
        
        // Remove all edges where this vertex is the source
        edgeCount -= adjacencyLists.get(vertexIndex).size();
        
        // Remove the vertex
        vertices.remove(vertexIndex);
        adjacencyLists.remove(vertexIndex);
        return true;
    }
    
    @Override
    public boolean removeEdge(V source, V destination) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Vertices cannot be null");
        }
        
        int sourceIndex = indexOf(source);
        if (sourceIndex < 0) {
            return false;
        }
        
        List<V> neighbors = adjacencyLists.get(sourceIndex);
        for (int i = 0; i < neighbors.size(); i++) {
            if (Objects.equals(neighbors.get(i), destination)) {
                neighbors.remove(i);
                edgeCount--;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Set<V> getVertices() {
        // Create a Set implementation that doesn't depend on java.util.HashSet
        // Here we use a simple array wrapper implementing the Set interface
        return new SimpleSet<>(vertices);
    }
    
    @Override
    public Set<V> getNeighbors(V vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex cannot be null");
        }
        
        int vertexIndex = indexOf(vertex);
        if (vertexIndex < 0) {
            throw new NoSuchElementException("Vertex does not exist");
        }
        
        // Get the list of neighbors
        List<V> neighbors = adjacencyLists.get(vertexIndex);
        return new SimpleSet<>(neighbors);
    }
    
    @Override
    public int getVertexCount() {
        return vertices.size();
    }
    
    @Override
    public int getEdgeCount() {
        return edgeCount;
    }
    
    @Override
    public boolean isEmpty() {
        return vertices.isEmpty();
    }
    
    @Override
    public void clear() {
        vertices.clear();
        adjacencyLists.clear();
        edgeCount = 0;
    }
    
    @Override
    public boolean putIfAbsent(V vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex cannot be null");
        }
        
        // Check if the vertex already exists
        boolean exists = containsVertex(vertex);
        
        // Add the vertex if it doesn't exist
        if (!exists) {
            addVertex(vertex);
        }
        
        // Return whether the vertex already existed
        return exists;
    }
    
    /**
     * Simple Set implementation, wrapping ArrayList
     */
    private static class SimpleSet<E> implements Set<E> {
        private final List<E> elements;
        
        public SimpleSet() {
            this.elements = new ArrayList<>();
        }
        
        public SimpleSet(Collection<E> collection) {
            this.elements = new ArrayList<>();
            for (E e : collection) {
                if (!contains(e)) {
                    this.elements.add(e);
                }
            }
        }
        
        @Override
        public int size() {
            return elements.size();
        }
        
        @Override
        public boolean isEmpty() {
            return elements.isEmpty();
        }
        
        @Override
        public boolean contains(Object o) {
            return elements.contains(o);
        }
        
        @Override
        public Iterator<E> iterator() {
            return elements.iterator();
        }
        
        @Override
        public Object[] toArray() {
            return elements.toArray();
        }
        
        @Override
        public <T> T[] toArray(T[] a) {
            return elements.toArray(a);
        }
        
        @Override
        public boolean add(E e) {
            if (contains(e)) {
                return false;
            }
            return elements.add(e);
        }
        
        @Override
        public boolean remove(Object o) {
            return elements.remove(o);
        }
        
        @Override
        public boolean containsAll(Collection<?> c) {
            return elements.containsAll(c);
        }
        
        @Override
        public boolean addAll(Collection<? extends E> c) {
            boolean modified = false;
            for (E e : c) {
                if (add(e)) {
                    modified = true;
                }
            }
            return modified;
        }
        
        @Override
        public boolean retainAll(Collection<?> c) {
            return elements.retainAll(c);
        }
        
        @Override
        public boolean removeAll(Collection<?> c) {
            return elements.removeAll(c);
        }
        
        @Override
        public void clear() {
            elements.clear();
        }
    }
}