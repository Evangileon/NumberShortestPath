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

    /**
     * Because all nodes are non-negative, use Dijkstra
     * @param source node
     */
    private void useDijkstraToFindShortestPathTree(int source) {
        // initialize
        for (Vertex v : vertices) {
            v.dis = Long.MAX_VALUE;
            v.pred = 0;
            v.inPath = false;
        }

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

    /**
     * Backtrace by pred field to find the shorted path to source, mark node.inPath = true
     * @param terminator specific node
     * @return the number of nodes in shortest path
     */
    public int backtraceToFindShortestPath(int terminator) {
        Vertex t = vertices.get(terminator);
        Vertex v = t;
        Vertex u;
        t.inPath = true;
        t.next = 0;
        int count = 1;

        while (v.pred != 0) {
            u = vertices.get(v.pred);
            u.inPath = true;
            u.next = v.index;
            v = u;
            count++;
        }

        return count;
    }

    public int computeMaximumReward(int source) {
        useDijkstraToFindShortestPathTree(source);


        return 0;
    }
 }
