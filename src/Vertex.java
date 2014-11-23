
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

    public Vertex(int index) {
        this.index = index;
    }

    public void addAdj(int adjIndex, int weight) {
        adj.add(adjIndex);
        adjWeight.add(weight);
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
