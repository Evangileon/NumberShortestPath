
import java.util.ArrayList;
import java.util.LinkedList;

public class Vertex implements Comparable<Vertex> {
    LinkedList<Integer> adj = new LinkedList<Integer>();
    LinkedList<Integer> adjWeight = new LinkedList<Integer>();

    final int index;
    boolean visited = false;
    long dis = Integer.MAX_VALUE;
    int pred = 0;

    long numPath = 0;

    // whether this node is in queue
    boolean inQueue = false;
    // visit count
    int count = 0;

    // whether this node is in shortest path from source to specific node
    boolean inPath = false;
    // next node in shortest path
    int next = 0;
    // possible reward at this node
    int reward = 0;
    // accumulated reward
    int accumReward = 0;
    // dead end
    boolean hasOutlet = true;

    // cycle
    int gainBonus = 0;
    long cycleReward = 0;
    int cyclePred = 0;
    // yet another cycle
    long reverseCycleReward = 0;
    int reverseCyclePred = 0;

    public Vertex(int index) {
        this.index = index;
    }

    public void addAdj(int adjIndex, int weight) {
        adj.add(adjIndex);
        adjWeight.add(weight);
    }

    ArrayList<Integer> preds = new ArrayList<Integer>();

    public void addPred(int pred) {
        preds.add(pred);
    }

    public boolean hasPred(int u) {
        return preds.contains(u);
    }

    @Override
    public int compareTo(Vertex o) {
        long diff = this.dis - o.dis;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
