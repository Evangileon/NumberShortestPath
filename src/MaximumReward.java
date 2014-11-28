import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        this.Q = new VertexMinHeap(vertices);

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
     * @return total reward of nodes in shortest path
     */
    public int backtraceToFindShortestPath(int terminator) {
        Vertex t = vertices.get(terminator);
        Vertex v = t;
        Vertex u;
        t.inPath = true;
        t.next = 0;
        int num = t.reward;

        while (v.pred != 0) {
            u = vertices.get(v.pred);
            u.inPath = true;
            u.next = v.index;
            v = u;
            num += u.reward;
        }

        return num;
    }

    /**
     * Whether start node has a path back to source, without using marked nodes (inPath == true)
     * @param start node
     * @return Whether start node has a path back to source
     */
    public boolean runDFSBackToSource(Vertex start, Vertex source) {

        if (start == source) {
            return true;
        }

        for (int v_index : start.adj) {
            Vertex v = vertices.get(v_index);

            if (!v.visited && !v.inPath && v != start) {
                v.visited = true;
                // if the sub-path can go to source
                boolean feasible = runDFSBackToSource(v, source);
                if (feasible) {
                    return true;
                }
            }
        }

        return false;
    }

    public void clearToInit() {
        for (int i = 1; i < vertices.size(); i++) {
            Vertex u = vertices.get(i);
            u.inPath = false;
            u.visited = false;
            u.reward = 0;
        }
    }

    public int computeMaximumReward(int source) {
        useDijkstraToFindShortestPathTree(source);

        ArrayList<Vertex> pathValueList = new ArrayList<Vertex>();
        for (int i = 1; i < vertices.size(); i++) {
            pathValueList.add(vertices.get(i));
        }

        // sort all nodes by their shortest distance from source
        Collections.sort(pathValueList, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                if (o1.dis == o2.dis) {
                    return 0;
                }
                return (o1.dis - o2.dis) > 0 ? 1 : -1;
            }
        });

        for (int i = pathValueList.size() - 1; i >= 0; i--) {
            clearToInit();

            Vertex u = pathValueList.get(i);

            int reward = backtraceToFindShortestPath(i);

            u.visited = true;
            if (runDFSBackToSource(u, vertices.get(source))) {
                return reward;
            }
        }

        return 0;
    }

    /**
     * Add undirected edge
     * @param from one end
     * @param to one end
     * @param weight weight
     */
    private void addEdge(int from, int to, int weight) {
        vertices.get(from).addAdj(to, weight);
        vertices.get(to).addAdj(from, weight);
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

            ArrayList<Vertex> nodeSet = new ArrayList<Vertex>(numNodes);
            nodeSet.add(new Vertex(0)); // 0 unused

            // fill node set with numNodes nodes
            for (int i = 1; i <= numNodes; i++) {
                nodeSet.add(new Vertex(i));
            }

            // construct
            MaximumReward solution = new MaximumReward(nodeSet);

            int rewardCount = 0;
            String line;
            // reward
            while ((line = reader.readLine()) != null && !line.equals("")) {
                String[] rewards = line.split("[\\s\\t]+");
                assert (numNodes - rewardCount) >= 0;
                int fix = rewardCount;
                for (; rewardCount < fix + rewards.length; rewardCount++) {
                    nodeSet.get(rewardCount + 1).reward = Integer.valueOf(rewards[rewardCount - fix]);
                }

                if (rewardCount == numNodes) {
                    break;
                }
            }

            int count = 0;

            while ((line = reader.readLine()) != null && !line.equals("")) {
                String[] params = line.split("[\\s\\t]+");
                int from = Integer.valueOf(params[0]);
                int to = Integer.valueOf(params[1]);
                int weight = Integer.valueOf(params[2]);

                solution.addEdge(from, to, weight);
                count++;
                if (count == numEdges) {
                    break;
                }
            }

            assert count == numEdges : "The number of edges claimed is not equal to actual one";

            long start = System.currentTimeMillis();
            int reward = solution.computeMaximumReward(source);
            long end = System.currentTimeMillis();

            System.out.printf("%d %d\n", reward, end - start);

//            if (!isApplicable) {
//                System.out.println("Non-positive cycle in graph.  DAC is not applicable");
//            } else {
//                System.out.printf("%d %d %d\n", solution.vertices.get(terminator).dis, solution.vertices.get(terminator).numPath, end - start);
////                if (solution.numNodes() <= 100) {
////                    solution.printNumShortestPath(source);
////                }
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 }
