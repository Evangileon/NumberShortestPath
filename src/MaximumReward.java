import java.io.*;
import java.util.*;


public class MaximumReward {

    private ArrayList<Vertex> vertices;
    class Edge {
        int from;
        int to;
        int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private ArrayList<Edge> edges;

    public MaximumReward(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
        this.edges = new ArrayList<Edge>();
    }

    public int numNodes() {
        return vertices.size() - 1;
    }

    /**
     * Compute shortest path and number of it from source to each nodes
     * @param source source node
     * @return the graph is a applicable graph, which means, no zero or negative cycle
     */
    public boolean computeNumberShortestPathToEachVertex(int source) {
        // initialize
        for (Vertex v : vertices) {
            v.dis = Long.MAX_VALUE;
            v.pred = 0;
            v.inQueue = false;
            v.count = 0;
        }

        Vertex s = vertices.get(source);
        s.dis = 0;

        LinkedList<Integer> queue = new LinkedList<Integer>();
        // source initialize
        queue.add(source);
        s.inQueue = true;
        s.numPath = 1;

        while (!queue.isEmpty()) {
            int u_index = queue.remove();
            Vertex u = vertices.get(u_index);
            u.inQueue = false;

            if (u.count >= numNodes()) {
                // has non-positive cycle
                return false;
            }

            Iterator<Integer> adjItor = u.adj.iterator();
            Iterator<Integer> weightItor = u.adjWeight.iterator();

            // for each adjacent node of u
            while (adjItor.hasNext()) {
                int v_index = adjItor.next();
                Vertex v = vertices.get(v_index);
                int u_v_weight = weightItor.next();

                boolean v_d_changed = relax(u, v, u_v_weight);

                if (v_d_changed && !v.inQueue) {
                    // v.d changed anf v is not in queue
                    queue.add(v_index);
                    v.inQueue = true;
                }
            }
        }

        return true;
    }

    /**
     * Relax path between u and v
     * @param u node
     * @param v node
     * @param u_v_weight weight(u, v)
     * @return whether v.d changed
     */
    private boolean relax(Vertex u, Vertex v, int u_v_weight) {

        boolean changed = false;

        if (v.dis > (u.dis + u_v_weight)) {
            // update number of paths, because previous shortest path is no longer in path
            //v.numPath = u.numPath;
            v.dis = u.dis + u_v_weight;
            v.preds.clear();
            v.addPred(u.index);
            changed = true;
        } else if (v.dis == (u.dis + u_v_weight)) {
            // another path whose distance is the same with shortest path
            //v.numPath += u.numPath;
            v.addPred(u.index);
        }

        return changed;
    }

    /**
     * Because all nodes are non-negative, use Dijkstra
     * @param source node
     */
    private void useDijkstraToFindShortestPathTree(int source) {
        VertexMinHeap q = new VertexMinHeap(vertices);

        // initialize
        for (Vertex v : vertices) {
            v.dis = Long.MAX_VALUE;
            v.preds.clear();
            v.inPath = false;
        }

        q.buildHeap();

        // source
        Vertex s = vertices.get(source);
        s.dis = 0;
        q.decreaseKey(source, 0);
        //s.accumReward = s.reward;

        while (!q.isEmpty()) {
            int u_index = q.deleteMin();
            Vertex u = vertices.get(u_index);
            u.visited = true;

//            if (u.index != source && u.preds.size() == 0) {
//                // unreachable from source
//                continue;
//            }

            Iterator<Integer> adjItor = u.adj.iterator();
            Iterator<Integer> weightItor = u.adjWeight.iterator();

            // for each adjacent node
            while (adjItor.hasNext()) {
                int v_index = adjItor.next();
                Vertex v = vertices.get(v_index);
                int u_v_weight = weightItor.next();

                if (!v.visited && (v.dis > (u.dis + u_v_weight))) {
                    if (v.dis > (u.dis + u_v_weight)) {
                        // update number of paths, because previous shortest path is no longer in path
                        v.dis = u.dis + u_v_weight;
                        q.decreaseKey(v_index, v.dis);
                        v.preds.clear();
                        v.addPred(u_index);
                    }
                }

                if ((v.dis == (u.dis + u_v_weight))) {
                    if (!v.hasPred(u_index)) {
                        v.addPred(u_index);
                    }
                }
            }
        }
    }

    /**
     * Backtrace from start, this procedure terminated when meet source node
     * @param start starting node
     * @param source s
     */
    private void backtraceToFindCycle(int start, int source) {
        maxCycle = new LinkedList<Integer>();
        maxCycleReward = new LinkedList<Integer>();
        maxCycle.addFirst(start);
        maxCycleReward.addFirst(vertices.get(start).gainBonus);

        Vertex v = vertices.get(start);
        Vertex u;

        while (v.cyclePred != source) {
            u = vertices.get(v.cyclePred);
            maxCycle.addFirst(u.index);
            maxCycleReward.addFirst(u.gainBonus);
            v = u;
        }

        maxCycle.addFirst(source);
        maxCycleReward.addFirst(vertices.get(source).reward);
    }

    long maxReward = Long.MIN_VALUE;
    LinkedList<Integer> maxCycle = new LinkedList<Integer>();
    LinkedList<Integer> maxCycleReward = new LinkedList<Integer>();

    /**
     * Perform DFS to find all cycles
     * @param start start node
     * @param parent parent of start node
     * @param source s
     * @param curReward reward from source to start node
     */
    private void depthFirstSearch(int start, int parent, int source, long curReward) {
        Vertex u = vertices.get(start);

        if (start == source) {

            // found a cycle
            if (maxReward < curReward) {
                maxReward = curReward;
                backtraceToFindCycle(start, source);
            }
            return;
        }

        for (int v_index : u.adj) {
            if (v_index == parent) {
                // ignore the the node previously accessed
                continue;
            }

            Vertex v = vertices.get(v_index);
            // follow the shortest path?
            int bonus = (v.hasPred(u.index)) ? v.reward : 0;

            if (!v.visited || v_index == source) {
                v.visited = true;
                v.cyclePred = u.index;
                // bonus gained this step
                v.gainBonus = bonus;
                depthFirstSearch(v.index, u.index, source, curReward + bonus);
                // rollback
                v.gainBonus = 0;
                v.cyclePred = 0;
                v.visited = false;
            }
        }
    }

    /**
     * Traverse all possible cycles to find maximum reward cycle
     * @param source s
     */
    public void runCycleEnumerate(int source) {
        for (Vertex v : vertices) {
            v.visited = false;
        }

        Vertex s = vertices.get(source);
        s.visited = true;
        long curReward = s.reward;

        for (int v_index : s.adj) {
            Vertex v = vertices.get(v_index);
            // follow the shortest path?
            int bonus = (v.hasPred(s.index)) ? v.reward : 0;

            if (!v.visited) {
                v.visited = true;
                v.cyclePred = s.index;
                // bonus gained this step
                v.gainBonus = bonus;
                depthFirstSearch(v.index, s.index, source, curReward + bonus);
                // rollback
                v.gainBonus = 0;
                v.cyclePred = 0;
                v.visited = false;
            }
        }
    }

    public long computeMaximumReward(int source) {
        useDijkstraToFindShortestPathTree(source);
        //computeNumberShortestPathToEachVertex(source);

        runCycleEnumerate(source);
        return maxReward;
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
        edges.add(new Edge(from, to, weight));
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
            long reward = solution.computeMaximumReward(source);
            long end = System.currentTimeMillis();

            System.out.printf("%d %d\n", reward, end - start);

            Iterator<Integer> itor = solution.maxCycle.iterator();
            Iterator<Integer> itorReward = solution.maxCycleReward.iterator();

            HashSet<Integer> set = new HashSet<Integer>();

            while (itor.hasNext()) {
                int v_index = itor.next();
                int v_reward = itorReward.next();
                if (set.contains(v_index) && v_index != source) {
                    System.out.println("error");
                    break;
                }
                set.add(v_index);
                Vertex v = solution.vertices.get(v_index);
                System.out.println(v_index + " " + v_reward);
            }

//            System.out.println("Print number of preds");
//
//            for (Vertex v : solution.vertices) {
//                System.out.println(v.index + " " + v.preds.size() + " " + Arrays.toString(v.preds.toArray()));
//            }

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
