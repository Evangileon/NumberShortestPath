import java.util.ArrayList;
import java.util.Iterator;


public class MaximumReward {

    private VertexMinHeap Q;
    private ArrayList<Vertex> vertices;


    public MaximumReward(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
    }

    public int numNodes() {
        return vertices.size() - 1;
    }

    private void useDijkstraToFindShortestPathTree(int source) {
        this.Q.buildHeap();

        // source
        vertices.get(source).dis = 0;
        Q.decreaseKey(source, 0);

        while (!Q.isEmpty()) {
            int u_index = Q.deleteMin();
            Vertex u = vertices.get(u_index);
            u.visited = true;

            if (u.index != source && u.pred == 0) {
                // unreachable from source
                continue;
            }

            Iterator<Integer> adjItor = u.adj.iterator();
            Iterator<Integer> weightItor = u.adjWeight.iterator();

            // for each adjacent node
            while (adjItor.hasNext()) {
                int v_index = adjItor.next();
                Vertex v = vertices.get(v_index);
                int u_v_weight = weightItor.next();

                if (!v.visited && (v.dis > (u.dis + u_v_weight))) {
                    // update number of paths, because previous shortest path is no longer in path
                    v.dis = u.dis + u_v_weight;
                    Q.decreaseKey(v_index, v.dis);
                    v.pred = u_index;
                }
            }
        }
    }
}
