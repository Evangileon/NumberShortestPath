import java.util.ArrayList;
import java.util.Iterator;

/**
 * Modify from Dijkstra Algorithm
 */
public class NumberShortestPath {
    private VertexMinHeap Q;
    private ArrayList<Vertex> vertices;

    public NumberShortestPath(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
        this.Q = new VertexMinHeap(vertices);
        this.Q.buildHeap();
    }

    /**
     * Compute shortest path and number of it from source to each nodes
     * @param source source node
     * @return the graph is a applicable graph, which means, no zero or negative cycle
     */
    public boolean computeNumberShortestPathToEachVertex(int source) {

        Q.decreaseKey(source, 0);
        vertices.get(source).numPath = 1;

        while (!Q.isEmpty()) {
            int u_index = Q.deleteMin();
            Vertex u = vertices.get(u_index);
            u.visited = true;

            Iterator<Integer> adjItor = u.adj.iterator();
            Iterator<Integer> weightItor = u.adjWeight.iterator();

            // for each adjacent node
            while (adjItor.hasNext()) {
                int v_index = adjItor.next();
                Vertex v = vertices.get(v_index);
                int u_v_weight = weightItor.next();

                if (!v.visited) {
                    if (v.dis > (u.dis + u_v_weight)) {
                        // update number of paths, because previous shortest path is no longer in path
                        v.numPath = u.numPath;
                        v.dis = u.dis + u_v_weight;
                        Q.decreaseKey(v_index, v.dis);
                        v.pred = u_index;
                    } else if (v.dis == (u.dis + u_v_weight)) {
                        // another path whose distance is the same with shortest path
                        v.numPath += u.numPath;
                    }
                }

                if (v.visited && (v.dis <= (u.dis + u_v_weight))) {
                    // if zero or negative exists, the visited node's distance
                    // is not larger than one of its predecessors
                    return false;
                }
            }
        }
        return true;
    }
}
