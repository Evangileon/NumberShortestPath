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
        Vertex s = vertices.get(source);
        s.dis = 0;
        Q.decreaseKey(source, 0);
        s.accumReward = s.reward;

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
                    v.accumReward = u.accumReward + v.reward;
                }
            }
        }
    }

    /**
     * Backtrace by pred field to find the shorted path to source, mark node.inPath = true
     * @param t specific node
     * @return total reward of nodes in shortest path
     */
    private int backtraceToFindShortestPath(Vertex t) {
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
     * @return -2 if no dead end, -1 if not path back to source, otherwise the maximum reward
     *          if follow this path
     */
    public int runDFSBackToSource(Vertex start, Vertex source) {

//        if (start == source) {
//            return 0;
//        }

        if (start.adj.size() == 1) {
            // only has one edge, which means this node is a dead end
            return -2;
        }

        boolean allAdjAreDeadEnd = true;
        int maxReward = -1;
        for (int v_index : start.adj) {
            Vertex v = vertices.get(v_index);
            if (v == source) {
                return 0;
            }

            if (!v.hasOutlet) {
                // skip the nodes that lead to dead end
                continue;
            }

            if (!v.visited && v != start) {
                v.visited = true;
                // if the sub-path can go to source
                int reward = runDFSBackToSource(v, source);
                // same direction with shortest path
                int bonus = (v.pred == start.index) ? v.reward : 0;
                if (reward != -2) {
                    allAdjAreDeadEnd = false;
                }
                if (reward >= 0) {
                    maxReward = Math.max(maxReward, reward + bonus);
                }
                v.visited = false;
            }
        }

        if (allAdjAreDeadEnd) {
            start.hasOutlet = false;
            return -2;
        }

        if (maxReward >= 0) {
            return maxReward;
        }

        return -1;
    }

    public void clearToInit() {
        for (int i = 1; i < vertices.size(); i++) {
            Vertex u = vertices.get(i);
            u.inPath = false;
            u.visited = false;
        }
    }

    public int computeMaximumReward(int source) {
        useDijkstraToFindShortestPathTree(source);

        clearToInit();

        return runDFSBackToSource(vertices.get(source), vertices.get(source));
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
