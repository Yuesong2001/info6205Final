package adt;
import java.util.Set;

public interface MyGraph<V> {
    /**
     * 添加顶点
     * @param vertex 要添加的顶点
     * @return 如果顶点成功添加则返回true，如果顶点已存在则返回false
     */
    boolean addVertex(V vertex);
    
    /**
     * 添加边
     * @param source 源顶点
     * @param destination 目标顶点
     * @return 如果边成功添加则返回true，如果边已存在则返回false
     */
    boolean addEdge(V source, V destination);
    
    /**
     * 检查顶点是否存在
     * @param vertex 顶点
     * @return 如果顶点存在则返回true，否则返回false
     */
    boolean containsVertex(V vertex);
    
    /**
     * 检查边是否存在
     * @param source 源顶点
     * @param destination 目标顶点
     * @return 如果边存在则返回true，否则返回false
     */
    boolean containsEdge(V source, V destination);
    
    /**
     * 删除顶点及其关联的所有边
     * @param vertex 要删除的顶点
     * @return 如果顶点被成功删除则返回true，如果顶点不存在则返回false
     */
    boolean removeVertex(V vertex);
    
    /**
     * 删除边
     * @param source 源顶点
     * @param destination 目标顶点
     * @return 如果边被成功删除则返回true，如果边不存在则返回false
     */
    boolean removeEdge(V source, V destination);
    
    /**
     * 获取所有顶点
     * @return 包含所有顶点的集合
     */
    Set<V> getVertices();
    
    /**
     * 获取顶点的所有邻居（直接相连的顶点）
     * @param vertex 顶点
     * @return 包含所有邻居顶点的集合
     */
    Set<V> getNeighbors(V vertex);
    
    /**
     * 获取图中的顶点数
     * @return 顶点数
     */
    int getVertexCount();
    
    /**
     * 获取图中的边数
     * @return 边数
     */
    int getEdgeCount();
    
    /**
     * 检查图是否为空（没有顶点）
     * @return 如果图为空则返回true，否则返回false
     */
    boolean isEmpty();
    
    /**
     * 清空图中的所有顶点和边
     */
    void clear();
    
    boolean putIfAbsent(V vertex);
}