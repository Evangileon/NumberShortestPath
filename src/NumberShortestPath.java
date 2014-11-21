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

    public void computeNumberShortestPathToEachVertex(int source) {

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

                if ((!v.visited) && (v.dis > (u.dis + u_v_weight))) {

                }
            }

        }
    }
}
