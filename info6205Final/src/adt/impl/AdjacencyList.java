// Graph接口保持不变

// AdjacencyList实现
package adt.impl;

import adt.MyGraph;
import java.util.*;

/**
 * 基于邻接表的图实现
 * 使用简单数组和列表避免内置HashMap和HashSet
 * @param <V> 顶点的类型
 */
public class AdjacencyList<V> implements MyGraph<V> {
    // 用于存储顶点
    private final List<V> vertices;
    // 用于存储每个顶点的邻居
    private final List<List<V>> adjacencyLists;
    private int edgeCount;
    
    /**
     * 创建一个空图
     */
    public AdjacencyList() {
        this.vertices = new ArrayList<>();
        this.adjacencyLists = new ArrayList<>();
        this.edgeCount = 0;
    }
    
    /**
     * 查找顶点索引
     * @param vertex 要查找的顶点
     * @return 顶点索引，如果不存在则返回-1
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
        
        // 如果顶点不存在，则添加
        if (!containsVertex(source)) {
            addVertex(source);
        }
        
        if (!containsVertex(destination)) {
            addVertex(destination);
        }
        
        // 检查边是否已存在
        if (containsEdge(source, destination)) {
            return false;
        }
        
        // 添加边
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
        
        // 删除以该顶点为终点的所有边
        for (int i = 0; i < adjacencyLists.size(); i++) {
            List<V> neighbors = adjacencyLists.get(i);
            for (int j = 0; j < neighbors.size(); j++) {
                if (Objects.equals(neighbors.get(j), vertex)) {
                    neighbors.remove(j);
                    edgeCount--;
                    j--; // 调整索引
                }
            }
        }
        
        // 删除以该顶点为起点的所有边
        edgeCount -= adjacencyLists.get(vertexIndex).size();
        
        // 删除顶点
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
        // 创建一个不依赖java.util.HashSet的Set实现
        // 这里使用简单数组包装实现Set接口
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
        
        // 获取邻居列表
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
        
        // 检查顶点是否已存在
        boolean exists = containsVertex(vertex);
        
        // 如果顶点不存在，则添加它
        if (!exists) {
            addVertex(vertex);
        }
        
        // 返回顶点是否已存在
        return exists;
    }
    
    /**
     * 简单的Set实现，包装ArrayList
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