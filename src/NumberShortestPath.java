import java.io.*;
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
    }

    /**
     * Compute shortest path and number of it from source to each nodes
     * @param source source node
     * @return the graph is a applicable graph, which means, no zero or negative cycle
     */
    public boolean computeNumberShortestPathToEachVertex(int source) {
        // build heap
        this.Q.buildHeap();

        // source
        Q.decreaseKey(source, 0);
        vertices.get(source).numPath = 1;

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

                if (v.visited && (v.dis <= (u.dis + u_v_weight)) && v.index != source) {
                    // if zero or negative exists, the visited node's distance
                    // is not larger than one of its predecessors
                    return false;
                }
            }
        }
        return true;
    }

    private void addEdge(int from, int to, int weight) {
        vertices.get(from).addAdj(to, weight);
    }

    public void printNumShortestPath(int source) {
        //System.out.println("1" + " 0" + " -" + " 1"); // for node 1

        for (int i = 1; i < vertices.size(); i++) {
            Vertex u = vertices.get(i);

            System.out.print(i + " ");
            if (u.pred != 0 || u.index == source) {
                System.out.print(u.dis + " ");

            } else {
                System.out.print("INF ");
            }
            if (u.pred != 0) {
                System.out.print(u.pred + " ");
            } else {
                System.out.print("- ");
            }
            System.out.println(u.numPath);
        }
    }

    public int numNodes() {
        return vertices.size() - 1;
    }

    public static void main(String[] args) {
        BufferedReader reader = null;

        if (args.length > 0) {
            try {
                reader = new BufferedReader(new FileReader(args[0]));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }

        assert reader != null;
        try {
            String metaLine = reader.readLine();
            String[] meta = metaLine.split("[\\s\\t]+");

            int numNodes = Integer.valueOf(meta[0]);
            int numEdges = Integer.valueOf(meta[1]);
            int source = Integer.valueOf(meta[2]);
            int terminator = Integer.valueOf(meta[3]);

            ArrayList<Vertex> nodeSet = new ArrayList<Vertex>(numNodes);
            nodeSet.add(new Vertex(0)); // 0 unused

            // fill node set with numNodes nodes
            for (int i = 1; i <= numNodes; i++) {
                nodeSet.add(new Vertex(i));
            }

            // construct
            NumberShortestPath solution = new NumberShortestPath(nodeSet);

            int count = 0;
            String line;
            while ((line = reader.readLine()) != null && !line.equals("")) {
                String[] params = line.split("[\\s\\t]+");
                int from = Integer.valueOf(params[0]);
                int to = Integer.valueOf(params[1]);
                int weight = Integer.valueOf(params[2]);

                solution.addEdge(from, to, weight);
                count++;
            }

            assert count == numEdges : "The number of edges claimed is not equal to actual one";

            long start = System.currentTimeMillis();
            boolean isApplicable = solution.computeNumberShortestPathToEachVertex(source);
            long end = System.currentTimeMillis();

            if (!isApplicable) {
                System.out.println("Non-positive cycle in graph.  DAC is not applicable");
            } else {
                System.out.printf("%d %d %d\n", solution.vertices.get(terminator).dis, solution.vertices.get(terminator).numPath, end - start);
                if (solution.numNodes() <= 100) {
                    solution.printNumShortestPath(source);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
